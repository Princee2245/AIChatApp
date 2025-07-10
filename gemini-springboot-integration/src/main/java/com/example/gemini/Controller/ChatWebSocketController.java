package com.example.gemini.Controller;

import com.example.gemini.Model.PromptMessage;
import com.example.gemini.Service.PromptProducer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;

import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

   @Autowired
   private PromptProducer promptProducer;

    @MessageMapping("/sendPrompt")
    public void handlePrompt(PromptMessage message) throws InterruptedException {
        JSONObject obj = new JSONObject();
        obj.put("prompt", message.getPrompt());
        obj.put("username", message.getUsername());
        obj.put("sessionId", message.getSessionId());

        promptProducer.sendPrompt(obj.toString());
    }
}