package com.example.gemini.Controller;

import com.example.gemini.Entity.ChatSession;
import com.example.gemini.Model.PromptMessage;
import com.example.gemini.Model.StreamMessage;
import com.example.gemini.Repository.ChatRepository;
import com.example.gemini.Repository.ChatSessionRepository;
import com.example.gemini.Repository.UserRepository;
import com.example.gemini.Entity.ChatEntry;
import com.example.gemini.Entity.User;
import com.example.gemini.Service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @MessageMapping("/sendPrompt")
    public void handlePrompt(PromptMessage message) throws InterruptedException {
        String prompt = message.getPrompt();
        String username = message.getUsername();
        Long sessionId = message.getSessionId();

        String fullResponse = geminiService.getGeminiResponse(prompt);

        // Simulate typing
        String[] words = fullResponse.split(" ");
        StringBuilder streamed = new StringBuilder();
        for (String word : words) {
            streamed.append(word).append(" ");
            messagingTemplate.convertAndSend("/topic/responses/" + username,
                    new StreamMessage(streamed.toString(), false));
            Thread.sleep(150);
        }

        // ✅ FIX: Check both user and session exist
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        // Save chat entry
        ChatEntry entry = new ChatEntry();
        entry.setPrompt(prompt);
        entry.setResponse(fullResponse.trim());
        entry.setUser(user);
        entry.setSession(session); // ✅ Important!
        entry.setTimestamp(LocalDateTime.now());

        chatRepository.save(entry);

        messagingTemplate.convertAndSend("/topic/responses/" + username,
                new StreamMessage(fullResponse.trim(), true));
    }

}