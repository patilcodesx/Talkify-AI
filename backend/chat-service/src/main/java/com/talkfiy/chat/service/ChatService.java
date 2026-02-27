package com.talkfiy.chat.service;

import com.talkfiy.chat.dto.request.AddReactionRequest;
import com.talkfiy.chat.dto.request.SendMessageRequest;
import com.talkfiy.chat.dto.response.MessageResponse;
import com.talkfiy.chat.model.Message;
import com.talkfiy.chat.repository.ConversationRepository;
import com.talkfiy.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request, Long senderId) {
        // Verify conversation exists
        conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Create and save message
        Message message = Message.builder()
                .conversationId(request.getConversationId())
                .senderId(senderId)
                .content(request.getContent())
                .type(request.getType())
                .language(request.getLanguage())
                .attachment(request.getAttachment())
                .parentMessageId(request.getParentMessageId())
                .read(false)
                .edited(false)
                .reactions(new ArrayList<>())
                .build();
        
        message = messageRepository.save(message);
        
        // Update conversation last message time
        conversationRepository.findById(request.getConversationId()).ifPresent(conv -> {
            conv.setLastMessageAt(LocalDateTime.now());
            conversationRepository.save(conv);
        });
        
        MessageResponse response = MessageResponse.fromMessage(message);
        
        // Broadcast message via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + request.getConversationId(),
                response
        );
        
        log.info("Message sent: {} in conversation: {}", message.getId(), request.getConversationId());
        return response;
    }
    
    public Page<MessageResponse> getConversationMessages(String conversationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByConversationIdOrderByTimestampDesc(conversationId, pageable)
                .map(MessageResponse::fromMessage);
    }
    
    public List<MessageResponse> searchMessages(String conversationId, String searchTerm) {
        return messageRepository.searchMessagesInConversation(conversationId, searchTerm)
                .stream()
                .map(MessageResponse::fromMessage)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MessageResponse addReaction(String messageId, AddReactionRequest request, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        List<Message.Reaction> reactions = message.getReactions();
        if (reactions == null) {
            reactions = new ArrayList<>();
        }
        
        // Find existing reaction with same emoji
        Message.Reaction existingReaction = reactions.stream()
                .filter(r -> r.getEmoji().equals(request.getEmoji()))
                .findFirst()
                .orElse(null);
        
        if (existingReaction != null) {
            // Toggle reaction
            if (existingReaction.getUserIds().contains(userId)) {
                existingReaction.getUserIds().remove(userId);
                if (existingReaction.getUserIds().isEmpty()) {
                    reactions.remove(existingReaction);
                }
            } else {
                existingReaction.getUserIds().add(userId);
            }
        } else {
            // Add new reaction
            List<Long> userIds = new ArrayList<>();
            userIds.add(userId);
            reactions.add(Message.Reaction.builder()
                    .emoji(request.getEmoji())
                    .userIds(userIds)
                    .build());
        }
        
        message.setReactions(reactions);
        message = messageRepository.save(message);
        
        MessageResponse response = MessageResponse.fromMessage(message);
        
        // Broadcast reaction update via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + message.getConversationId() + "/reactions",
                response
        );
        
        return response;
    }
    
    @Transactional
    public void markMessagesAsRead(String conversationId, Long userId) {
        List<Message> unreadMessages = messageRepository.findByConversationIdOrderByTimestampDesc(
                conversationId, PageRequest.of(0, 100)
        ).getContent().stream()
                .filter(m -> !m.getSenderId().equals(userId) && !m.getRead())
                .collect(Collectors.toList());
        
        unreadMessages.forEach(m -> m.setRead(true));
        messageRepository.saveAll(unreadMessages);
        
        // Notify sender via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + conversationId + "/read",
                userId
        );
    }
    
    public long getUnreadCount(String conversationId, Long userId) {
        return messageRepository.countByConversationIdAndSenderIdNotAndReadFalse(conversationId, userId);
    }
}
