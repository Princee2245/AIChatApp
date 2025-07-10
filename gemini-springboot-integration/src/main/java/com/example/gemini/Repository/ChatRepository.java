package com.example.gemini.Repository;

import com.example.gemini.Entity.ChatEntry;
import com.example.gemini.Entity.ChatSession;
import com.example.gemini.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatEntry, Long> {
    List<ChatEntry> findByUserOrderByTimestampDesc(User user);
    List<ChatEntry> findBySessionOrderByTimestampDesc(ChatSession session);
    @Transactional
    void deleteBySession(ChatSession session);

    @Query("SELECT c.user.username, COUNT(c) FROM ChatEntry c GROUP BY c.user.username")
    List<Object[]> countChatsByUser();
}
