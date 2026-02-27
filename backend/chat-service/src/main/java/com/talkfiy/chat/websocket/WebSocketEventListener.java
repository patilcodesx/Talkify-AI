package com.talkfiy.chat.websocket;

import com.talkfiy.chat.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    
    private final PresenceService presenceService;
    
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = headerAccessor.getSessionAttributes() != null 
                ? (String) headerAccessor.getSessionAttributes().get("userId") 
                : null;
        
        if (userId != null) {
            presenceService.setOnline(Long.parseLong(userId));
            log.info("User connected: {}", userId);
        }
    }
    
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = headerAccessor.getSessionAttributes() != null 
                ? (String) headerAccessor.getSessionAttributes().get("userId") 
                : null;
        
        if (userId != null) {
            presenceService.setOffline(Long.parseLong(userId));
            log.info("User disconnected: {}", userId);
        }
    }
}
