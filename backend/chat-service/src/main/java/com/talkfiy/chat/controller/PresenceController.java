package com.talkfiy.chat.controller;

import com.talkfiy.chat.dto.request.UpdatePresenceRequest;
import com.talkfiy.chat.dto.response.UserPresenceResponse;
import com.talkfiy.chat.service.PresenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PresenceController {
    
    private final PresenceService presenceService;
    
    @PutMapping
    public ResponseEntity<UserPresenceResponse> updatePresence(
            @Valid @RequestBody UpdatePresenceRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        UserPresenceResponse response = presenceService.updatePresence(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<UserPresenceResponse> getUserPresence(
            @PathVariable Long userId) {
        UserPresenceResponse response = presenceService.getUserPresence(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/online")
    public ResponseEntity<List<UserPresenceResponse>> getOnlineUsers() {
        List<UserPresenceResponse> users = presenceService.getOnlineUsers();
        return ResponseEntity.ok(users);
    }
    
    @PostMapping("/typing/{conversationId}")
    public ResponseEntity<Void> setTyping(
            @PathVariable String conversationId,
            @RequestParam boolean isTyping,
            @RequestHeader("X-User-Id") Long userId) {
        presenceService.setTyping(userId, conversationId, isTyping);
        return ResponseEntity.ok().build();
    }
}
