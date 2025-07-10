package com.example.gemini.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor

public class StreamMessage {
    private String content;
    private boolean done;
}
