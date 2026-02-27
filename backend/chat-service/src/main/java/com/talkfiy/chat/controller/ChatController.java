package com.talkfiy.chat.controller;

import com.talkfiy.chat.dto.request.AddReactionRequest;
import com.talkfiy.chat.dto.request.SendMessageRequest;
import com.talkfiy.chat.dto.response.MessageResponse;
import com.talkfiy.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {
    
    private final ChatService chatService;
    
    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        MessageResponse response = chatService.sendMessage(request, userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Page<MessageResponse>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<MessageResponse> messages = chatService.getConversationMessages(conversationId, page, size);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/conversations/{conversationId}/messages/search")
    public ResponseEntity<List<MessageResponse>> searchMessages(
            @PathVariable String conversationId,
            @RequestParam String query) {
        List<MessageResponse> messages = chatService.searchMessages(conversationId, query);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/messages/{messageId}/reactions")
    public ResponseEntity<MessageResponse> addReaction(
            @PathVariable String messageId,
            @Valid @RequestBody AddReactionRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        MessageResponse response = chatService.addReaction(messageId, request, userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") Long userId) {
        chatService.markMessagesAsRead(conversationId, userId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/conversations/{conversationId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") Long userId) {
        long count = chatService.getUnreadCount(conversationId, userId);
        return ResponseEntity.ok(count);
    }
}
