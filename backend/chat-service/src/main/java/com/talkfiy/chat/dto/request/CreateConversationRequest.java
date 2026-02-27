package com.talkfiy.chat.dto.request;

import com.talkfiy.chat.model.Conversation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    
    @NotBlank(message = "Conversation name is required")
    private String name;
    
    @NotNull(message = "Conversation type is required")
    private Conversation.ConversationType type;
    
    private String avatar;
    
    private String description;
    
    @NotEmpty(message = "At least one participant is required")
    private Set<Long> participantIds;
}
