package ch.sbb.mail.service;

import ch.sbb.atlas.model.mail.MailNotification;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = "atlas.mail", groupId = "atlas.mail.consumer")
public class MailListener {

  private final MailService mailService;

  @KafkaHandler
  public void receiveMailSendingRequest(@Valid MailNotification mailNotification) {
    mailService.sendEmailWithHtmlTemplate(mailNotification);
  }

}
