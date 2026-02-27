package com.talkfiy.chat.repository;

import com.talkfiy.chat.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
    
    @Query("SELECT c FROM Conversation c WHERE :userId MEMBER OF c.participantIds AND c.isActive = true ORDER BY c.lastMessageAt DESC")
    List<Conversation> findUserConversations(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Conversation c WHERE c.type = 'DM' AND :userId1 MEMBER OF c.participantIds AND :userId2 MEMBER OF c.participantIds")
    Optional<Conversation> findDMConversation(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
    
    @Query("SELECT c FROM Conversation c WHERE c.type = 'CHANNEL' AND c.isActive = true")
    List<Conversation> findAllChannels();
    
    @Query("SELECT c FROM Conversation c WHERE :userId MEMBER OF c.participantIds AND c.name LIKE %:searchTerm%")
    List<Conversation> searchConversations(@Param("userId") Long userId, @Param("searchTerm") String searchTerm);
}
