package com.talkfiy.chat.service;

import com.talkfiy.chat.dto.request.CreateConversationRequest;
import com.talkfiy.chat.dto.response.ConversationResponse;
import com.talkfiy.chat.model.Conversation;
import com.talkfiy.chat.repository.ConversationRepository;
import com.talkfiy.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {
    
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    
    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request, Long creatorId) {
        // Add creator to participants
        Set<Long> participants = new HashSet<>(request.getParticipantIds());
        participants.add(creatorId);
        
        // For DM, check if conversation already exists
        if (request.getType() == Conversation.ConversationType.DM) {
            if (participants.size() != 2) {
                throw new RuntimeException("DM conversation must have exactly 2 participants");
            }
            
            List<Long> participantList = new ArrayList<>(participants);
            var existingConv = conversationRepository.findDMConversation(participantList.get(0), participantList.get(1));
            if (existingConv.isPresent()) {
                return ConversationResponse.fromConversation(existingConv.get());
            }
        }
        
        Conversation conversation = Conversation.builder()
                .name(request.getName())
                .type(request.getType())
                .avatar(request.getAvatar())
                .description(request.getDescription())
                .participantIds(participants)
                .createdBy(creatorId)
                .isActive(true)
                .build();
        
        conversation = conversationRepository.save(conversation);
        log.info("Conversation created: {} by user: {}", conversation.getId(), creatorId);
        
        return ConversationResponse.fromConversation(conversation);
    }
    
    public List<ConversationResponse> getUserConversations(Long userId) {
        return conversationRepository.findUserConversations(userId).stream()
                .map(conv -> {
                    ConversationResponse response = ConversationResponse.fromConversation(conv);
                    // Get unread count
                    long unreadCount = messageRepository.countByConversationIdAndSenderIdNotAndReadFalse(
                            conv.getId(), userId
                    );
                    response.setUnreadCount(unreadCount);
                    return response;
                })
                .collect(Collectors.toList());
    }
    
    public ConversationResponse getConversation(String conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Verify user is participant
        if (!conversation.getParticipantIds().contains(userId)) {
            throw new RuntimeException("User is not a participant of this conversation");
        }
        
        ConversationResponse response = ConversationResponse.fromConversation(conversation);
        long unreadCount = messageRepository.countByConversationIdAndSenderIdNotAndReadFalse(
                conversationId, userId
        );
        response.setUnreadCount(unreadCount);
        
        return response;
    }
    
    @Transactional
    public ConversationResponse addParticipant(String conversationId, Long participantId, Long requesterId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Verify requester is participant or creator
        if (!conversation.getParticipantIds().contains(requesterId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        if (conversation.getType() == Conversation.ConversationType.DM) {
            throw new RuntimeException("Cannot add participants to DM conversation");
        }
        
        conversation.getParticipantIds().add(participantId);
        conversation = conversationRepository.save(conversation);
        
        log.info("Participant {} added to conversation {}", participantId, conversationId);
        return ConversationResponse.fromConversation(conversation);
    }
    
    @Transactional
    public void removeParticipant(String conversationId, Long participantId, Long requesterId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Verify requester is creator or removing themselves
        if (!conversation.getCreatedBy().equals(requesterId) && !participantId.equals(requesterId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        conversation.getParticipantIds().remove(participantId);
        conversationRepository.save(conversation);
        
        log.info("Participant {} removed from conversation {}", participantId, conversationId);
    }
    
    public List<ConversationResponse> searchConversations(Long userId, String searchTerm) {
        return conversationRepository.searchConversations(userId, searchTerm).stream()
                .map(ConversationResponse::fromConversation)
                .collect(Collectors.toList());
    }
}
