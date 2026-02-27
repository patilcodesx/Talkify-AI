package com.talkfiy.chat.dto.request;

import com.talkfiy.chat.model.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    
    @NotBlank(message = "Conversation ID is required")
    private String conversationId;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    @NotNull(message = "Message type is required")
    private Message.MessageType type;
    
    private String language; // For code messages
    
    private Message.FileAttachment attachment;
    
    private String parentMessageId; // For replies
}
