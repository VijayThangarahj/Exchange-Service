package com.careandshare.exchange.Service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from:no-reply@careandshare.com}")
    private String fromEmail;

    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            System.out.println("Email sent successfully to: " + to);
        } catch (Exception e) {
            System.err.println("Failed to send email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            System.out.println("HTML email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.err.println("Failed to send HTML email to " + to + ": " + e.getMessage());
            throw new RuntimeException("Failed to send HTML email: " + e.getMessage(), e);
        }
    }

    public void sendExchangeApprovalEmail(String to, String exchangerName, String ownerName, String itemTitle, String recipientType) {
        String subject = "Exchange Request Approved - CareAndShare";

        String content;
        if ("owner".equals(recipientType)) {
            content = String.format(
                    "Dear %s,\n\n" +
                            "Someone is willing to exchange your product '%s'.\n\n" +
                            "Exchanger Name: %s\n" +
                            "You can now chat with the exchanger to arrange the exchange details.\n\n" +
                            "Click the chat button on our website to start communicating.\n\n" +
                            "Best regards,\nCareAndShare Team",
                    ownerName, itemTitle, exchangerName
            );
        } else {
            content = String.format(
                    "Dear %s,\n\n" +
                            "Admin approves your exchange request for '%s'.\n\n" +
                            "Item Owner: %s\n" +
                            "You can now chat with the item owner to arrange the exchange details.\n\n" +
                            "Click the chat button on our website to start communicating.\n\n" +
                            "Best regards,\nCareAndShare Team",
                    exchangerName, itemTitle, ownerName
            );
        }

        sendSimpleEmail(to, subject, content);
    }

    // Test method to verify email configuration
    public void testEmailConfiguration() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo("test@example.com");
            message.setSubject("Test Email from CareAndShare");
            message.setText("This is a test email to verify the email configuration.");

            mailSender.send(message);
            System.out.println("Test email sent successfully!");
        } catch (Exception e) {
            System.err.println("Test email failed: " + e.getMessage());
            throw new RuntimeException("Email configuration test failed: " + e.getMessage(), e);
        }
    }
}