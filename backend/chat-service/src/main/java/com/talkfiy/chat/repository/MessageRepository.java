package com.talkfiy.chat.repository;

import com.talkfiy.chat.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    
    Page<Message> findByConversationIdOrderByTimestampDesc(String conversationId, Pageable pageable);
    
    List<Message> findByConversationIdAndTimestampAfter(String conversationId, LocalDateTime timestamp);
    
    @Query("{ 'conversationId': ?0, 'content': { $regex: ?1, $options: 'i' } }")
    List<Message> searchMessagesInConversation(String conversationId, String searchTerm);
    
    long countByConversationIdAndSenderIdNotAndReadFalse(String conversationId, Long senderId);
    
    void deleteByConversationId(String conversationId);
}
