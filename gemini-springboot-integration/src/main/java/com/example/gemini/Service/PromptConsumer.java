package com.example.gemini.Service;

import com.example.gemini.Entity.ChatEntry;
import com.example.gemini.Entity.ChatSession;
import com.example.gemini.Entity.User;
import com.example.gemini.Model.StreamMessage;
import com.example.gemini.Repository.ChatRepository;
import com.example.gemini.Repository.ChatSessionRepository;
import com.example.gemini.Repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PromptConsumer {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "gemini-prompts", groupId = "gemini-group")
    public void listen(String messageJson) throws InterruptedException {
        JSONObject message = new JSONObject(messageJson);
        String prompt = message.getString("prompt");
        String username = message.getString("username");
        Long sessionId = message.getLong("sessionId");

        String fullResponse = geminiService.getGeminiResponse(prompt);

        // Stream response word-by-word
        String[] words = fullResponse.split(" ");
        StringBuilder streamed = new StringBuilder();
        for (String word : words) {
            streamed.append(word).append(" ");
            messagingTemplate.convertAndSend("/topic/responses/" + username,
                    new StreamMessage(streamed.toString(), false));
            Thread.sleep(150);
        }

        // Save to DB
        User user = userRepository.findByUsername(username).orElseThrow();
        ChatSession session = chatSessionRepository.findById(sessionId).orElseThrow();

        ChatEntry entry = new ChatEntry();
        entry.setPrompt(prompt);
        entry.setResponse(fullResponse.trim());
        entry.setUser(user);
        entry.setSession(session);
        entry.setTimestamp(LocalDateTime.now());

        chatRepository.save(entry);

        // Final response
        messagingTemplate.convertAndSend("/topic/responses/" + username,
                new StreamMessage(fullResponse.trim(), true));
    }
}