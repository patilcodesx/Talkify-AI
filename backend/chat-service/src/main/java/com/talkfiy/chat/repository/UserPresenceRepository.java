package com.talkfiy.chat.repository;

import com.talkfiy.chat.model.UserPresence;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPresenceRepository extends CrudRepository<UserPresence, Long> {
    
    List<UserPresence> findByStatus(UserPresence.UserStatus status);
    
    List<UserPresence> findByStatusNot(UserPresence.UserStatus status);
}
