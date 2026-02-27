package com.talkfiy.chat.controller;

import com.talkfiy.chat.dto.request.CreateConversationRequest;
import com.talkfiy.chat.dto.response.ConversationResponse;
import com.talkfiy.chat.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConversationController {
    
    private final ConversationService conversationService;
    
    @PostMapping
    public ResponseEntity<ConversationResponse> createConversation(
            @Valid @RequestBody CreateConversationRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        ConversationResponse response = conversationService.createConversation(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getUserConversations(
            @RequestHeader("X-User-Id") Long userId) {
        List<ConversationResponse> conversations = conversationService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }
    
    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationResponse> getConversation(
            @PathVariable String conversationId,
            @RequestHeader("X-User-Id") Long userId) {
        ConversationResponse response = conversationService.getConversation(conversationId, userId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{conversationId}/participants/{participantId}")
    public ResponseEntity<ConversationResponse> addParticipant(
            @PathVariable String conversationId,
            @PathVariable Long participantId,
            @RequestHeader("X-User-Id") Long userId) {
        ConversationResponse response = conversationService.addParticipant(conversationId, participantId, userId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{conversationId}/participants/{participantId}")
    public ResponseEntity<Void> removeParticipant(
            @PathVariable String conversationId,
            @PathVariable Long participantId,
            @RequestHeader("X-User-Id") Long userId) {
        conversationService.removeParticipant(conversationId, participantId, userId);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ConversationResponse>> searchConversations(
            @RequestParam String query,
            @RequestHeader("X-User-Id") Long userId) {
        List<ConversationResponse> results = conversationService.searchConversations(userId, query);
        return ResponseEntity.ok(results);
    }
}
