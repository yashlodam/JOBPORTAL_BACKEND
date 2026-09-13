package com.jobportal.chat.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.chat.dto.request.SendMessageRequest;
import com.jobportal.chat.dto.request.TypingIndicatorRequest;
import com.jobportal.chat.dto.response.MessageResponse;
import com.jobportal.chat.dto.response.ReadReceiptResponse;
import com.jobportal.chat.dto.response.TypingResponse;
import com.jobportal.chat.dto.response.WebSocketErrorResponse;
import com.jobportal.chat.entity.Conversation;
import com.jobportal.chat.entity.ConversationParticipant;
import com.jobportal.chat.entity.Message;
import com.jobportal.chat.mapper.ChatMapper;
import com.jobportal.chat.repository.ConversationParticipantRepository;
import com.jobportal.chat.service.ChatService;
import com.jobportal.domain.NotificationPriority;
import com.jobportal.domain.NotificationType;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.NotificationService;

/**
 * STOMP WebSocket controller that handles real-time chat message events.
 *
 * Endpoints:
 *   /app/chat.send    - send a message to a conversation
 *   /app/chat.typing  - broadcast a typing indicator to a conversation
 *   /app/chat.read    - mark a conversation as read and broadcast receipt
 *
 * Security model:
 *   - principal.getName() gives the sender email (set by JwtChannelInterceptor).
 *   - conversationId is taken from the payload, but access is always validated
 *     via ChatService.validateAndGetConversation() before any operation.
 *   - senderId is NEVER taken from the payload.
 *
 * Error handling:
 *   - @MessageExceptionHandler catches all exceptions and routes structured
 *     errors to /user/queue/errors on the sending client only.
 *   - No raw exception messages are exposed to the client.
 *
 * Notification integration:
 *   - On every new message, a MESSAGE_RECEIVED in-app notification is sent
 *     to each OTHER participant who is currently offline.
 *   - Online participants receive the message via WebSocket directly.
 */
@Controller
public class ChatWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketController.class);

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final com.jobportal.config.JwtProvider jwtProvider;

    public ChatWebSocketController(
            ChatService chatService,
            SimpMessagingTemplate messagingTemplate,
            UserRepository userRepository,
            com.jobportal.config.JwtProvider jwtProvider) {
        this.chatService       = chatService;
        this.messagingTemplate = messagingTemplate;
        this.userRepository    = userRepository;
        this.jwtProvider       = jwtProvider;
    }

    // ── Send Message ─────────────────────────────────────────────────────

    /**
     * Handle a send-message request from a WebSocket client.
     * Persists message, sends notifications to offline participants,
     * and broadcasts MessageResponse over STOMP to /topic/conversations/{convId}.
     */
    @MessageMapping("/chat.send")
    public void sendMessage(
            @Payload SendMessageRequest request,
            Principal principal,
            SimpMessageHeaderAccessor headerAccessor) throws JobPortalException {

        String senderEmail = (principal != null) ? principal.getName() : null;

        // Fallback 1: Extract from session attributes populated by HandshakeInterceptor
        if (senderEmail == null && headerAccessor != null && headerAccessor.getSessionAttributes() != null) {
            Object attr = headerAccessor.getSessionAttributes().get("authenticatedEmail");
            if (attr instanceof String s && !s.isBlank()) {
                senderEmail = s;
            }
        }

        // Fallback 2: Extract from native Authorization header
        if (senderEmail == null && headerAccessor != null) {
            String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    senderEmail = jwtProvider.getEmailFromToken(authHeader.substring(7));
                } catch (Exception ignored) {
                    // invalid header token
                }
            }
        }

        if (senderEmail == null) {
            log.warn("sendMessage rejected: unauthenticated WebSocket session for convId={}",
                request != null ? request.getConversationId() : null);
            return;
        }

        Long convId = request.getConversationId();

        // Validate, save, notify offline participants, and map to response
        MessageResponse response = chatService.sendMessage(convId, request.getContent(), senderEmail);

        // Broadcast to all subscribers of this conversation
        messagingTemplate.convertAndSend(
            "/topic/conversations/" + convId,
            response
        );
        log.debug("Message id=[{}] broadcast to /topic/conversations/[{}]",
            response.getId(), convId);
    }

    // ── Typing Indicator ─────────────────────────────────────────────────

    /**
     * Handle a typing indicator from a WebSocket client.
     * NOT persisted — purely real-time ephemeral state.
     *
     * Client subscribes to: /topic/conversations/{conversationId}
     * to receive typing events from other participants.
     */
    @MessageMapping("/chat.typing")
    public void handleTyping(
            @Payload TypingIndicatorRequest request,
            Principal principal) throws JobPortalException {

        if (principal == null) return;
        String senderEmail = principal.getName();
        Long convId = request.getConversationId();

        // Security: verify user is a participant before broadcasting
        if (!chatService.validateAndGetConversation(convId, senderEmail).getId().equals(convId)) {
            log.warn("Typing indicator rejected: [{}] is not a participant of conv [{}]",
                senderEmail, convId);
            return;
        }

        userRepository.findByEmail(senderEmail).ifPresent(user -> {
            TypingResponse typingResponse = new TypingResponse(
                convId,
                user.getId(),
                user.getName(),
                request.isTyping()
            );
            messagingTemplate.convertAndSend(
                "/topic/conversations/" + convId + "/typing",
                typingResponse
            );
        });
    }

    // ── Mark as Read ─────────────────────────────────────────────────────

    /**
     * Handle a mark-read event from a WebSocket client.
     * Updates lastReadAt and broadcasts a ReadReceipt to the conversation.
     * This allows the sender to see when the other party read their messages.
     *
     * Client subscribes to: /topic/conversations/{conversationId}/read
     * to receive read receipts.
     *
     * Note: the payload only carries conversationId.
     * The reader identity comes from the JWT principal — never from the payload.
     */
    @MessageMapping("/chat.read")
    @Transactional
    public void markAsRead(
            @Payload SendMessageRequest request,
            Principal principal) throws JobPortalException {

        if (principal == null) return;
        String readerEmail = principal.getName();
        Long convId = request.getConversationId();

        chatService.markAsRead(convId, readerEmail);

        userRepository.findByEmail(readerEmail).ifPresent(user -> {
            ReadReceiptResponse receipt = new ReadReceiptResponse(
                convId,
                user.getId(),
                user.getName(),
                java.time.LocalDateTime.now()
            );
            messagingTemplate.convertAndSend(
                "/topic/conversations/" + convId + "/read",
                receipt
            );
            log.debug("Read receipt broadcast for conv [{}] by [{}]", convId, readerEmail);
        });
    }

    // ── Error Handling ───────────────────────────────────────────────────

    /**
     * Catch all exceptions from @MessageMapping methods.
     * Routes a structured error ONLY to the client that caused it,
     * via /user/queue/errors (private per-user queue).
     * No raw stack traces or internal error messages are exposed.
     */
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public WebSocketErrorResponse handleException(Exception ex) {
        log.error("WebSocket message handler exception: {}", ex.getMessage(), ex);
        if (ex instanceof JobPortalException jpe) {
            int status = jpe.getHttpStatus() != null ? jpe.getHttpStatus().value() : 500;
            if (status == 403) return WebSocketErrorResponse.accessDenied();
            if (status == 400) return WebSocketErrorResponse.invalidPayload(jpe.getMessage());
        }
        return WebSocketErrorResponse.serverError();
    }
}
