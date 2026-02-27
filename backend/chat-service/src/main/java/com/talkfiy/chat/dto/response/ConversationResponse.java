package com.talkfiy.chat.dto.response;

import com.talkfiy.chat.model.Conversation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponse {
    
    private String id;
    private String name;
    private Conversation.ConversationType type;
    private String avatar;
    private String description;
    private Set<Long> participantIds;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastMessageAt;
    private Boolean isActive;
    private String lastMessage;
    private Long unreadCount;
    
    public static ConversationResponse fromConversation(Conversation conversation) {
        return ConversationResponse.builder()
                .id(conversation.getId())
                .name(conversation.getName())
                .type(conversation.getType())
                .avatar(conversation.getAvatar())
                .description(conversation.getDescription())
                .participantIds(conversation.getParticipantIds())
                .createdBy(conversation.getCreatedBy())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .lastMessageAt(conversation.getLastMessageAt())
                .isActive(conversation.getIsActive())
                .build();
    }
}
