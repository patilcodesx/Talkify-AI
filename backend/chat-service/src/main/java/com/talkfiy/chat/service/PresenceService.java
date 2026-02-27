package com.talkfiy.chat.service;

import com.talkfiy.chat.dto.request.UpdatePresenceRequest;
import com.talkfiy.chat.dto.response.UserPresenceResponse;
import com.talkfiy.chat.model.UserPresence;
import com.talkfiy.chat.repository.UserPresenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceService {
    
    private final UserPresenceRepository presenceRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    public UserPresenceResponse updatePresence(Long userId, UpdatePresenceRequest request) {
        UserPresence presence = presenceRepository.findById(userId)
                .orElse(UserPresence.builder()
                        .userId(userId)
                        .build());
        
        presence.setStatus(request.getStatus());
        presence.setCustomStatus(request.getCustomStatus());
        presence.setLastSeenAt(LocalDateTime.now());
        
        presence = presenceRepository.save(presence);
        
        UserPresenceResponse response = UserPresenceResponse.fromUserPresence(presence);
        
        // Broadcast presence update
        messagingTemplate.convertAndSend("/topic/presence", response);
        
        log.info("Presence updated for user: {} to status: {}", userId, request.getStatus());
        return response;
    }
    
    public UserPresenceResponse getUserPresence(Long userId) {
        return presenceRepository.findById(userId)
                .map(UserPresenceResponse::fromUserPresence)
                .orElse(UserPresenceResponse.builder()
                        .userId(userId)
                        .status(UserPresence.UserStatus.OFFLINE)
                        .build());
    }
    
    public List<UserPresenceResponse> getOnlineUsers() {
        return presenceRepository.findByStatus(UserPresence.UserStatus.ONLINE).stream()
                .map(UserPresenceResponse::fromUserPresence)
                .collect(Collectors.toList());
    }
    
    public void setTyping(Long userId, String conversationId, boolean isTyping) {
        UserPresence presence = presenceRepository.findById(userId)
                .orElse(UserPresence.builder()
                        .userId(userId)
                        .status(UserPresence.UserStatus.ONLINE)
                        .build());
        
        presence.setIsTyping(isTyping);
        presence.setTypingInConversation(isTyping ? conversationId : null);
        presenceRepository.save(presence);
        
        // Broadcast typing indicator
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId + "/typing",
                UserPresenceResponse.fromUserPresence(presence)
        );
    }
    
    public void setOnline(Long userId) {
        UserPresence presence = presenceRepository.findById(userId)
                .orElse(UserPresence.builder()
                        .userId(userId)
                        .build());
        
        presence.setStatus(UserPresence.UserStatus.ONLINE);
        presence.setLastSeenAt(LocalDateTime.now());
        presenceRepository.save(presence);
        
        messagingTemplate.convertAndSend("/topic/presence", UserPresenceResponse.fromUserPresence(presence));
    }
    
    public void setOffline(Long userId) {
        UserPresence presence = presenceRepository.findById(userId)
                .orElse(UserPresence.builder()
                        .userId(userId)
                        .build());
        
        presence.setStatus(UserPresence.UserStatus.OFFLINE);
        presence.setLastSeenAt(LocalDateTime.now());
        presence.setIsTyping(false);
        presence.setTypingInConversation(null);
        presenceRepository.save(presence);
        
        messagingTemplate.convertAndSend("/topic/presence", UserPresenceResponse.fromUserPresence(presence));
    }
}
