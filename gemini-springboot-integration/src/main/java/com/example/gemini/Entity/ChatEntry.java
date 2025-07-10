package com.example.gemini.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Setter
public class ChatEntry {

    // Getters and setters
    @Id
    @GeneratedValue
    private Long id;

    private String prompt;
    private String response;
    private LocalDateTime timestamp;
    @ManyToOne
    private ChatSession session;

    @ManyToOne
    private User user;

    public ChatEntry() {
        this.timestamp = LocalDateTime.now();
    }


}
