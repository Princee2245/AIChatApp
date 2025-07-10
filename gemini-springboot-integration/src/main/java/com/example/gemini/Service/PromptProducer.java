package com.example.gemini.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PromptProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "gemini-prompts";

    public void sendPrompt(String message) {
        kafkaTemplate.send(TOPIC, message);
    }
}

