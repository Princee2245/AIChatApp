package com.example.gemini.Repository;

import com.example.gemini.Entity.ChatSession;
import com.example.gemini.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatSessionRepository extends JpaRepository<ChatSession,Long> {
    List<ChatSession> findByUser(User user);
}
