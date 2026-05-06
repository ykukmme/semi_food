package com.semi.controller;

import com.semi.domain.mail.MailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendMail(@Valid @RequestBody MailRequest request) {
        try {
            mailService.sendSimpleMail(request.getTo(), request.getSubject(), request.getText());
            return ResponseEntity.ok(Map.of("success", true, "message", "메일이 발송되었습니다."));
        } catch (Exception e) {
            log.error("메일 발송 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "메일 발송 실패: " + e.getMessage()));
        }
    }

    @PostMapping("/send-html")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> sendHtmlMail(@Valid @RequestBody MailRequest request) {
        try {
            mailService.sendHtmlMail(request.getTo(), request.getSubject(), request.getText());
            return ResponseEntity.ok(Map.of("success", true, "message", "HTML 메일이 발송되었습니다."));
        } catch (MessagingException e) {
            log.error("HTML 메일 발송 실패: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "메일 발송 실패: " + e.getMessage()));
        }
    }

    public static class MailRequest {
        @NotBlank
        @Email
        private String to;

        @NotBlank
        private String subject;

        @NotBlank
        private String text;

        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}
