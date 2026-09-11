package com.jobportal.chat.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.chat.dto.request.CreateConversationRequest;
import com.jobportal.chat.dto.response.ChatUnreadCountResponse;
import com.jobportal.chat.dto.response.ConversationResponse;
import com.jobportal.chat.dto.response.MessageResponse;
import com.jobportal.chat.service.ChatService;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.exception.JobPortalException;

import jakarta.validation.Valid;

/**
 * REST controller for chat conversations.
 *
 * Routes:
 *   POST   /api/chat/conversations               - create or get existing conversation
 *   GET    /api/chat/conversations               - list my conversations
 *   GET    /api/chat/conversations/{id}          - get one conversation
 *   GET    /api/chat/conversations/{id}/messages - paginated messages (newest first)
 *   PATCH  /api/chat/conversations/{id}/read     - mark messages as read
 *   GET    /api/chat/unread-count                - total unread badge count
 *   DELETE /api/chat/conversations/{id}/messages/{msgId} - soft-delete a message
 *
 * All endpoints use authentication.getName() to extract the caller email from JWT.
 * No userId is ever accepted from the request body or path for ownership operations.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    // ── Create / Get Conversation ────────────────────────────────────────

    /**
     * Create a new conversation or return the existing one between the same two users.
     * Idempotent: calling this twice with the same participantId returns the same conversation.
     */
    @PostMapping("/conversations")
    public ResponseEntity<ApiResponse<ConversationResponse>> createOrGetConversation(
            @Valid @RequestBody CreateConversationRequest request,
            Authentication authentication) throws JobPortalException {
        ConversationResponse response = chatService.createOrGetConversation(
            request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Conversation ready.", response));
    }

    // ── List Conversations ───────────────────────────────────────────────

    /**
     * Get all conversations for the authenticated user, sorted by most recent message.
     * Each conversation includes: lastMessage preview, myUnreadCount, otherParticipant details.
     */
    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getMyConversations(
            Authentication authentication) throws JobPortalException {
        List<ConversationResponse> list = chatService.getMyConversations(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // ── Single Conversation ──────────────────────────────────────────────

    /**
     * Get full details of one conversation.
     * Returns 403 if the authenticated user is not a participant.
     */
    @GetMapping("/conversations/{id}")
    public ResponseEntity<ApiResponse<ConversationResponse>> getConversation(
            @PathVariable Long id,
            Authentication authentication) throws JobPortalException {
        ConversationResponse response = chatService.getConversation(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── Messages ─────────────────────────────────────────────────────────

    /**
     * Get paginated messages for a conversation, newest first.
     *
     * Default: page=0, size=30.
     * Page 0 = 30 newest messages. Page 1 = next 30 older messages.
     * Frontend should reverse the list to display newest at the bottom.
     *
     * Returns 403 if caller is not a participant.
     */
    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<ApiResponse<Page<MessageResponse>>> getMessages(
            @PathVariable Long id,
            @PageableDefault(size = 30) Pageable pageable,
            Authentication authentication) throws JobPortalException {
        Page<MessageResponse> messages = chatService.getMessages(id, authentication.getName(), pageable);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    // ── Mark as Read ─────────────────────────────────────────────────────

    /**
     * Mark all messages in a conversation as read for the authenticated user.
     * Updates ConversationParticipant.lastReadAt = now.
     * Returns 403 if caller is not a participant.
     */
    @PatchMapping("/conversations/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            Authentication authentication) throws JobPortalException {
        chatService.markAsRead(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.message("Conversation marked as read."));
    }

    // ── Unread Count ─────────────────────────────────────────────────────

    /**
     * Total unread message count across all conversations for the authenticated user.
     * Poll this to keep the Messages nav badge up to date.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<ChatUnreadCountResponse>> getUnreadCount(
            Authentication authentication) throws JobPortalException {
        ChatUnreadCountResponse count = chatService.getTotalUnreadCount(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // ── Delete Message ───────────────────────────────────────────────────

    /**
     * Soft-delete a message. Only the original sender can delete their own message.
     * Deleted messages display as 'This message was deleted.' in the UI.
     * Returns 403 if the caller is not the sender.
     */
    @DeleteMapping("/conversations/{id}/messages/{msgId}")
    public ResponseEntity<ApiResponse<MessageResponse>> deleteMessage(
            @PathVariable Long id,
            @PathVariable Long msgId,
            Authentication authentication) throws JobPortalException {
        MessageResponse deleted = chatService.deleteMessage(id, msgId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Message deleted.", deleted));
    }
}
