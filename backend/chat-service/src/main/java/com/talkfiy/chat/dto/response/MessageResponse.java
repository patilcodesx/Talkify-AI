package com.talkfiy.chat.dto.response;

import com.talkfiy.chat.model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    
    private String id;
    private String conversationId;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private String content;
    private LocalDateTime timestamp;
    private Message.MessageType type;
    private String language;
    private List<Message.Reaction> reactions;
    private Boolean read;
    private Message.FileAttachment attachment;
    private Boolean edited;
    private LocalDateTime editedAt;
    private String parentMessageId;
    
    public static MessageResponse fromMessage(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversationId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .type(message.getType())
                .language(message.getLanguage())
                .reactions(message.getReactions())
                .read(message.getRead())
                .attachment(message.getAttachment())
                .edited(message.getEdited())
                .editedAt(message.getEditedAt())
                .parentMessageId(message.getParentMessageId())
                .build();
    }
}
