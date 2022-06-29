package ch.sbb.mail.service;


import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;

import ch.sbb.mail.exception.MailSendException;
import ch.sbb.mail.model.MailNotification;
import ch.sbb.mail.model.MailTemplateConfig;
import java.nio.charset.StandardCharsets;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

  private static final String ATLAS_SENDER = "TechSupport-ATLAS@sbb.ch";

  private final JavaMailSender emailMailSender;

  private final MailContentBuilder mailContentBuilder;

  public void sendEmailWithHtmlTemplate(MailNotification mailNotification) {
    MimeMessagePreparator mimeMessagePreparator = mimeMessage ->
        mailContentBuilder.prepareMessageHelper(mailNotification, mimeMessage);
    try {
      emailMailSender.send(mimeMessagePreparator);
      log.info(format("Mail sent: %s ", mailNotification));
    } catch (MailException e) {
      log.error("Mail {} not sent. {}", mailNotification, e.getLocalizedMessage());
      throw new MailSendException(e.getLocalizedMessage());
    }
  }

  public void sendSimpleMail(MailNotification mailNotification) {
    SimpleMailMessage mailMessage = new SimpleMailMessage();
    mailMessage.setTo(mailNotification.toAsArray());
    mailMessage.setFrom(getSender(mailNotification));
    mailMessage.setSubject(mailNotification.getSubject());
    mailMessage.setText(mailNotification.getContent());
    try {
      emailMailSender.send(mailMessage);
      log.info(format("Mail sent: %s ", mailNotification));
    } catch (MailException e) {
      log.error("Mail {} not sent. {}", mailNotification, e.getLocalizedMessage());
      throw new MailSendException(e.getLocalizedMessage());
    }
  }

  private String getSender(MailNotification mailNotification) {
    if (mailNotification.getFrom() != null && !mailNotification.getFrom().isEmpty()) {
      return mailNotification.getFrom();
    }
    return ATLAS_SENDER;
  }

}
