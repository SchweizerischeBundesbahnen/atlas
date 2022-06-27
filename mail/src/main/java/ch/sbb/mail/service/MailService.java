package ch.sbb.mail.service;


import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import ch.sbb.mail.exception.MailSendException;
import ch.sbb.mail.model.MailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

  private String getSender(MailNotification mailNotification){
    if(mailNotification.getFrom() != null && !mailNotification.getFrom().isEmpty()){
      return mailNotification.getFrom();
    }
    return ATLAS_SENDER;
  }

  public void sendSimpleMail(MailNotification mailNotification) {
    validateMail(mailNotification);
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

  public void sendEmailWithHtmlTemplate(MailNotification mailNotification) {
    validateMail(mailNotification);
    MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
      messageHelper.setFrom(getSender(mailNotification));
      messageHelper.setTo(mailNotification.toAsArray());
      messageHelper.setSubject(mailNotification.getSubject());
      String htmlContent = mailContentBuilder.buildHtmlContent(mailNotification.getContent());
      messageHelper.setText(htmlContent, true);
    };
    try {
      emailMailSender.send(mimeMessagePreparator);
      log.info(format("Mail sent: %s ", mailNotification));
    } catch (MailException e) {
      log.error("Mail {} not sent. {}", mailNotification, e.getLocalizedMessage());
      throw new MailSendException(e.getLocalizedMessage());
    }
  }

  private void validateMail(MailNotification mail) {
    requireNonNull(mail);
    requireNonNull(mail.getTo());
    requireNonNull(mail.getSubject());
    requireNonNull(mail.getContent());
  }

}
