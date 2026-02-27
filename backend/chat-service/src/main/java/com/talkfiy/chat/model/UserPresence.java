package com.talkfiy.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash(value = "UserPresence", timeToLive = 3600) // 1 hour TTL
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPresence {
    
    @Id
    private Long userId;
    
    private UserStatus status;
    
    private String customStatus;
    
    private LocalDateTime lastSeenAt;
    
    private Boolean isTyping;
    
    private String typingInConversation;
    
    public enum UserStatus {
        ONLINE, AWAY, DND, OFFLINE
    }
}
