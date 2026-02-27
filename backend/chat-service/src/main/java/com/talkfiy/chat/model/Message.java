package com.talkfiy.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @Id
    private String id;
    
    @Indexed
    private String conversationId;
    
    @Indexed
    private Long senderId;
    
    private String content;
    
    @CreatedDate
    private LocalDateTime timestamp;
    
    private MessageType type;
    
    private String language; // For code messages
    
    private List<Reaction> reactions;
    
    private Boolean read;
    
    private FileAttachment attachment; // For file messages
    
    private Boolean edited;
    
    private LocalDateTime editedAt;
    
    private String parentMessageId; // For replies/threads
    
    public enum MessageType {
        TEXT, CODE, SYSTEM, FILE, IMAGE
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reaction {
        private String emoji;
        private List<Long> userIds;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileAttachment {
        private String fileName;
        private String fileUrl;
        private String fileType;
        private Long fileSize;
    }
}
