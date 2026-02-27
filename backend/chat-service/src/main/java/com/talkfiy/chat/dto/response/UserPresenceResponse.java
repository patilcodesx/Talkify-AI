package com.talkfiy.chat.dto.response;

import com.talkfiy.chat.model.UserPresence;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPresenceResponse {
    
    private Long userId;
    private UserPresence.UserStatus status;
    private String customStatus;
    private LocalDateTime lastSeenAt;
    private Boolean isTyping;
    private String typingInConversation;
    
    public static UserPresenceResponse fromUserPresence(UserPresence presence) {
        if (presence == null) {
            return null;
        }
        return UserPresenceResponse.builder()
                .userId(presence.getUserId())
                .status(presence.getStatus())
                .customStatus(presence.getCustomStatus())
                .lastSeenAt(presence.getLastSeenAt())
                .isTyping(presence.getIsTyping())
                .typingInConversation(presence.getTypingInConversation())
                .build();
    }
}
