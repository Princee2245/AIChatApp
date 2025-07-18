package com.example.gemini.Service;



import com.example.gemini.Entity.ChatEntry;
import com.example.gemini.Repository.ChatRepository;
import com.example.gemini.Repository.UserRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    private final String API_KEY; // Make it final and non-static
    private final String URL;     // Also final and non-static

    // Inject API_KEY via constructor
    public GeminiService(@Value("${gemini.api.key}") String apiKey) {
        this.API_KEY = apiKey;
        this.URL = String.format("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=%s", API_KEY);
    }


    public String getGeminiResponseAndSave(String prompt,String username) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = String.format("""
        {
          "contents": [
            {
              "parts": [
                { "text": "%s" }
              ]
            }
          ]
        }
        """, prompt.replace("\"", "\\\""));

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(URL, entity, String.class);

            JSONObject json = new JSONObject(response.getBody());
            String responseText= json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            ChatEntry chat=new ChatEntry();
            chat.setPrompt(prompt);
            chat.setResponse(responseText);
            chat.setUser(userRepository.findByUsername(username).orElseThrow());
            chatRepository.save(chat);
            return responseText;

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String getGeminiResponse(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("""
        {
          "contents": [
            {
              "parts": [
                { "text": "%s" }
              ]
            }
          ]
        }
        """, prompt.replace("\"", "\\\""));

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(URL, entity, String.class);
            JSONObject json = new JSONObject(response.getBody());
            return json
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
