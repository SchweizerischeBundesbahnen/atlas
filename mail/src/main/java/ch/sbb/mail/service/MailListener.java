package ch.sbb.mail.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "${kafka.atlas.mail.topic}", groupId = "${kafka.atlas.mail.groupId}")
public class MailListener {

  private final MailService mailService;

  @KafkaHandler
  public void receiveMailSendingRequest(@Valid MailNotification mailNotification) {
    mailService.sendEmailWithHtmlTemplate(mailNotification);
  }

}
