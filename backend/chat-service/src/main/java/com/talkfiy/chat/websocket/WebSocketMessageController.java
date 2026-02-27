package com.talkfiy.chat.websocket;

import com.talkfiy.chat.dto.request.SendMessageRequest;
import com.talkfiy.chat.dto.response.MessageResponse;
import com.talkfiy.chat.service.ChatService;
import com.talkfiy.chat.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketMessageController {
    
    private final ChatService chatService;
    private final PresenceService presenceService;
    
    /**
     * Send message via WebSocket
     * Client sends to: /app/chat.send
     * Broadcast to: /topic/conversation/{conversationId}
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest message, SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            chatService.sendMessage(message, Long.parseLong(userId));
        }
    }
    
    /**
     * Typing indicator
     * Client sends to: /app/chat.typing/{conversationId}
     * Broadcast to: /topic/conversation/{conversationId}/typing
     */
    @MessageMapping("/chat.typing/{conversationId}")
    public void typing(
            @DestinationVariable String conversationId,
            @Payload boolean isTyping,
            SimpMessageHeaderAccessor headerAccessor) {
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        if (userId != null) {
            presenceService.setTyping(Long.parseLong(userId), conversationId, isTyping);
        }
    }
}
