package com.example.gemini.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PromptMessage {
    private String username;
    private String prompt;
    private Long sessionId;
}
