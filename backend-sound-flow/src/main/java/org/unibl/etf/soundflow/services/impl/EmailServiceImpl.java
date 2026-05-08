package org.unibl.etf.soundflow.services.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.unibl.etf.soundflow.models.entities.ClientEntity;
import org.unibl.etf.soundflow.models.entities.VerifyTokenEntity;
import org.unibl.etf.soundflow.repositories.VerifyTokenEntityRepository;
import org.unibl.etf.soundflow.services.EmailService;
import org.unibl.etf.soundflow.util.EmailConstants;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    private final VerifyTokenEntityRepository verifyTokenEntityRepository;
    private final JavaMailSender mailSender;

    public EmailServiceImpl(VerifyTokenEntityRepository verifyTokenEntityRepository, JavaMailSender mailSender) {
        this.verifyTokenEntityRepository = verifyTokenEntityRepository;
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(ClientEntity recipient) {
        String token = generateVerifyToken(recipient);
        String endpoint = EmailConstants.VERIFICATION_ENDPOINT
                + "?token=" + token;
        String body = EmailConstants.VERIFICATION_MESSAGE
                + "<br><a href=\"" + endpoint + "\">Verify Account</a>";
        sendEmail(recipient, EmailConstants.VERIFICATION_SUBJECT, body);
    }

    @Override
    public void sendResetPasswordEmail(ClientEntity recipient) {
        String token = generateVerifyToken(recipient);
        String endpoint = EmailConstants.RESET_PASSWORD_ENDPOINT
                + "?token=" + token;
        String body = EmailConstants.RESET_PASSWORD_MESSAGE
                + "<br><a href=\"" + endpoint + "\">Reset Password</a>";
        sendEmail(recipient, EmailConstants.RESET_PASSWORD_SUBJECT, body);
    }

    private void sendEmail(ClientEntity recipient, String subject, String body) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String htmlMsg = EmailConstants.greeting(recipient.getName())
                    + body
                    + EmailConstants.FOOTER;

            helper.setTo(recipient.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlMsg, true);
            mailSender.send(mimeMessage);
        } catch(MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String generateVerifyToken(ClientEntity recipient) {
        String token = UUID.randomUUID().toString();
        VerifyTokenEntity verifyTokenEntity = new VerifyTokenEntity(
                null, token, Instant.now().plusSeconds(86400), recipient
        );
        verifyTokenEntityRepository.saveAndFlush(verifyTokenEntity);
        return token;
    }
}
