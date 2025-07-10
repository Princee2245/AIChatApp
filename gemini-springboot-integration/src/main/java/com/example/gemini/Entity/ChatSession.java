package com.example.gemini.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<ChatEntry> entries;

    public ChatSession() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters...
}
