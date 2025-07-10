package com.example.gemini.Controller;

import com.example.gemini.Entity.ChatEntry;
import com.example.gemini.Entity.ChatSession;
import com.example.gemini.Entity.User;
import com.example.gemini.Repository.ChatRepository;
import com.example.gemini.Repository.ChatSessionRepository;
import com.example.gemini.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatRepository chatRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        long totalUsers = userRepository.count();
        long totalSessions = chatSessionRepository.count();
        long totalChats = chatRepository.count();

        List<Object[]> userChatCounts = chatRepository.countChatsByUser();

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalSessions", totalSessions);
        model.addAttribute("totalChats", totalChats);
        model.addAttribute("userChatCounts", userChatCounts); // (username, count)

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/sessions")
    public String getAllSession(Model model){
        List<ChatSession> list=chatSessionRepository.findAll();
        model.addAttribute("sessions",list);
        return "admin/sessions";
    }

    @GetMapping("/sessions/{sessionId}")
    public String viewSessionChats(@PathVariable Long sessionId, Model model, Principal principal) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        List<ChatEntry> entries = chatRepository.findBySessionOrderByTimestampDesc(session);
        model.addAttribute("chatEntries", entries);
        model.addAttribute("sessionTitle", session.getTitle());
        model.addAttribute("username", session.getUser().getUsername());

        return "admin/session_chats"; // will match session_chats.html
    }

    @PostMapping("/sessions/delete")
    public String deleteSessionAsAdmin(@RequestParam Long sessionId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        chatRepository.deleteBySession(session); // delete all entries
        chatSessionRepository.delete(session);   // delete session

        return "redirect:/admin/sessions";
    }
}

