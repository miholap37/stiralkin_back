package com.mailsender.mailSender.services;

import com.mailsender.mailSender.requests.MailRequest;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendSimpleMessage(MailRequest mailRequest) throws MessagingException;
}
