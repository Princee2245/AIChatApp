package com.example.gemini.Controller;

import com.example.gemini.Entity.ChatEntry;
import com.example.gemini.Entity.ChatSession;
import com.example.gemini.Entity.User;
import com.example.gemini.Repository.ChatRepository;
import com.example.gemini.Repository.ChatSessionRepository;
import com.example.gemini.Repository.UserRepository;
import com.example.gemini.Service.GeminiService;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @GetMapping("/chat")
    public String chat(@RequestParam(value = "sessionId", required = false) Long sessionId,Model model, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        model.addAttribute("username", principal.getName());


        List<ChatSession> sessions = chatSessionRepository.findByUser(user);
        model.addAttribute("sessions", sessions);
        model.addAttribute("isAdmin", user.getRole().equals("ADMIN"));
//        System.out.println(principal.getName());

        ChatSession selectedSession = null;
        List<ChatEntry> history = List.of();

        if (sessionId != null) {
            selectedSession = chatSessionRepository.findById(sessionId).orElse(null);
            if (selectedSession != null && selectedSession.getUser().equals(user)) {
                history = chatRepository.findBySessionOrderByTimestampDesc(selectedSession);
            }
        }

        model.addAttribute("selectedSession", selectedSession);
//        System.out.println(sessionId);
        model.addAttribute("history", history);

        return "chat";
    }


    @PostMapping("/chat/session")
    public String createSession(@RequestParam String title, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        ChatSession session = new ChatSession();
        session.setTitle(title);
        session.setUser(user);
        chatSessionRepository.save(session);

        return "redirect:/chat?sessionId=" + session.getId();
    }


    @PostMapping("/chat/session/edit")
    public String editSessionTitle(@RequestParam Long sessionId,
                                   @RequestParam String newTitle,
                                   Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        ChatSession session = chatSessionRepository.findById(sessionId)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Session not found or unauthorized"));

        session.setTitle(newTitle);
        chatSessionRepository.save(session);

        return "redirect:/chat?sessionId=" + sessionId;
    }

    @PostMapping("/chat/session/delete")
    @Transactional
    public String deleteSession(@RequestParam Long sessionId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        ChatSession session = chatSessionRepository.findById(sessionId)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Session not found or unauthorized"));

        // Delete all chat entries related to the session
        chatRepository.deleteBySession(session);
        // Delete the session itself
        chatSessionRepository.delete(session);

        return "redirect:/chat";
    }

    @GetMapping("/chat/export/json")
    public ResponseEntity<?> exportSessionAsJson(@RequestParam Long sessionId, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        ChatSession session = chatSessionRepository.findById(sessionId)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Session not found or unauthorized"));

        List<ChatEntry> entries = chatRepository.findBySessionOrderByTimestampDesc(session);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=session-" + sessionId + ".json")
                .contentType(MediaType.APPLICATION_JSON)
                .body(entries);
    }

    @GetMapping("/chat/export/pdf")
    public ResponseEntity<byte[]> exportSessionAsPdf(@RequestParam Long sessionId, Principal principal) throws IOException {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        ChatSession session = chatSessionRepository.findById(sessionId)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Session not found or unauthorized"));

        List<ChatEntry> entries = chatRepository.findBySessionOrderByTimestampDesc(session);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Chat Session: " + session.getTitle()));
        document.add(new Paragraph("Created At: " + session.getCreatedAt().toString()));
        document.add(new Paragraph(" "));

        for (ChatEntry entry : entries) {
            document.add(new Paragraph("Prompt: " + entry.getPrompt()).setBold());
            document.add(new Paragraph("Response: " + entry.getResponse()));
            document.add(new Paragraph("Timestamp: " + entry.getTimestamp().toString()));
            document.add(new Paragraph(" "));
        }

        document.close();

        byte[] pdfBytes = out.toByteArray();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=session-" + sessionId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/chat/entry/delete")
    public String deleteEntry(@RequestParam Long entryId,
                              @RequestParam Long sessionId,
                              Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();

        ChatEntry entry = chatRepository.findById(entryId)
                .filter(e -> e.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Entry not found or unauthorized"));

        chatRepository.delete(entry);

        return "redirect:/chat?sessionId=" + sessionId;
    }
}
