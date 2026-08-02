package com.jobportal.dto.response;

/**
 * Summary returned by GET /api/notifications/unread-count.
 * Drives the notification bell badge on the frontend.
 */
public class UnreadCountResponse {

    private long unreadCount;

    public UnreadCountResponse() {}

    public UnreadCountResponse(long unreadCount) {
        this.unreadCount = unreadCount;
    }

    public long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(long unreadCount) { this.unreadCount = unreadCount; }
}
