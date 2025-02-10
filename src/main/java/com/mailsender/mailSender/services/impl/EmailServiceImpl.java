package com.mailsender.mailSender.services.impl;

import com.mailsender.mailSender.requests.MailRequest;
import com.mailsender.mailSender.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine templateEngine;

    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${mail.toEmail}")
    private String toEmail;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    public void sendSimpleMessage (MailRequest mailRequest) throws MessagingException {
        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject("Заявка");
        Context context = new Context();
        context.setVariable("name", mailRequest.getName());
        context.setVariable("phone", mailRequest.getPhone());
        context.setVariable("age", mailRequest.getAge());
        context.setVariable("problem", mailRequest.getProblem());
        context.setVariable("brand", mailRequest.getBrand());
        context.setVariable("service", mailRequest.getService());
        context.setVariable("discountName", mailRequest.getDiscountName());
        String processedString = templateEngine.process("message", context);
        mimeMessageHelper.setText(processedString, true);
        emailSender.send(mimeMessage);
        LOG.info("Email sent successfully: name {}, phone {}", mailRequest.getName(), mailRequest.getPhone());
    }
}
