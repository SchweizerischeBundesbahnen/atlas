package ch.sbb.mail.service;

import static java.lang.String.format;

import ch.sbb.mail.exception.MailSendException;
import ch.sbb.atlas.kafka.model.mail.MailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
      log.info("Mail with subject {} sent to {}", mailNotification.getSubject(), mailNotification.getTo());
    } catch (MailException e) {
      log.error("Mail {} not sent.", mailNotification, e);
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
      log.info("Simple Mail with subject {} sent to {}", mailNotification.getSubject(), mailNotification.getTo());
    } catch (MailException e) {
      log.error("Simple Mail {} not sent.", mailNotification, e);
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
