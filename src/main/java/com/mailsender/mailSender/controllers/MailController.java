package com.mailsender.mailSender.controllers;

import com.mailsender.mailSender.annotations.RateLimit;
import com.mailsender.mailSender.requests.MailRequest;
import com.mailsender.mailSender.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mail")
public class MailController {

    private final EmailService emailService;

    public MailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    // dDeprecated annotation RateLimit
    @RateLimit
    public ResponseEntity<String> sendMail(@Valid @RequestBody MailRequest mailRequest) throws MessagingException {
        emailService.sendSimpleMessage(mailRequest);
        return ResponseEntity.ok("Mail sent successfully");
    }
}
