package ch.sbb.mail.service;

import ch.sbb.mail.exception.MailSendException;
import ch.sbb.atlas.model.mail.MailNotification;
import ch.sbb.mail.model.MailTemplateConfig;
import ch.sbb.atlas.model.mail.MailType;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmailServiceIntegrationTest {

  @RegisterExtension
  static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
      .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
      .withPerMethodLifecycle(true);

  @Autowired
  private MailService mailService;

  @Test
  public void shouldSendSimpleMail() throws MessagingException, IOException {
    //given
    MailNotification mail = createMail();

    //when
    mailService.sendSimpleMail(mail);

    //then
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertThat(receivedMessages).hasSize(1);

    MimeMessage current = receivedMessages[0];

    assertThat(mail.getSubject()).isEqualTo(current.getSubject());
    assertThat(mail.getTo()).contains(current.getAllRecipients()[0].toString());
    assertThat(valueOf(current.getContent()).contains(mail.getContent())).isTrue();

  }

  @Test
  public void shouldThrowExceptionWhenSmtpServerIsDown() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("as@cc.ch")
                                            .subject("Hello")
                                            .to(singletonList("as@cc.ch"))
                                            .build();
    //when
    mailService.sendSimpleMail(mail);

  }

  @Test
  public void shouldThrowExceptionWhenSendSimpleEmailHasNotRecipientAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("aa@bb.ch")
                                            .subject("Hello")
                                            .to(new ArrayList<>())
                                            .build();

    //when
    assertThatExceptionOfType(MailSendException.class).isThrownBy(
        () -> mailService.sendSimpleMail(mail));

  }

  @Test
  public void shouldThrowExceptionWhenSendSimpleEmailHasNotWellFormedRecipientAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("as@cc.ch")
                                            .subject("Hello")
                                            .to(singletonList("123as   }$§d!!0"))
                                            .build();

    //when
    assertThatExceptionOfType(MailSendException.class).isThrownBy(
        () -> mailService.sendSimpleMail(mail));

  }

  @Test
  public void shouldSendEmailWithTUHtmlTemplate() throws MessagingException, IOException {
    //given

    List<Map<String, Object>> templateProperties = new ArrayList<>();
    Map<String, Object> objectMap1 = new HashMap<>();
    objectMap1.put("action", "UPDATED");
    objectMap1.put("csvTuNumber", "#0001");
    objectMap1.put("csvHrName", "Schweizerische Bundesbahnen SBB");
    objectMap1.put("dbTuNumber", "#0001");
    objectMap1.put("dbHrNumber", "Schweizerische Bundesbahnen SBB");
    Map<String, Object> objectMap2 = new HashMap<>();
    objectMap2.put("action", "CREATED");
    objectMap2.put("csvTuNumber", "#0002");
    objectMap2.put("csvHrName", "AlpTransit Gotthard AG");
    objectMap2.put("dbTuNumber", "#0002");
    objectMap2.put("dbHrNumber", "AlpTransit Gotthard AG");
    templateProperties.add(objectMap1);
    templateProperties.add(objectMap2);

    MailNotification mail = MailNotification.builder()
                                            .mailType(MailType.TU_IMPORT)
                                            .to(singletonList("a@b.c"))
                                            .from("to-be@ignored.ch")
                                            .subject("To be ignored")
                                            .content("To be ignored")
                                            .templateProperties(templateProperties).build();

    //when
    mailService.sendEmailWithHtmlTemplate(mail);

    //then
    MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
    assertThat(receivedMessages).hasSize(1);

    MimeMessage current = receivedMessages[0];

    assertThat(current.getSubject()).isEqualTo(
        MailTemplateConfig.getMailTemplateConfig(mail.getMailType()).getSubject());
    assertThat(current.getAllRecipients()).hasSize(1);
    assertThat(mail.getTo()).contains(current.getAllRecipients()[0].toString());
    assertThat(valueOf(current.getContent()).contains(mail.getContent())).isFalse();

  }

  @Test
  public void shouldThrowExceptionWhenSendSimpleEmailHasNotWellFormedFromAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .subject("Hello")
                                            .to(singletonList("123as   }$§d!!0"))
                                            .build();

    //when
    assertThatExceptionOfType(MailSendException.class).isThrownBy(
        () -> mailService.sendSimpleMail(mail));

  }

  @Test
  public void shouldThrowExceptionWhenSendEmailWithHtmlTemplateHasNotRecipientAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("aa@bb.ch")
                                            .subject("Hello")
                                            .to(new ArrayList<>())
                                            .build();

    //when
    assertThatExceptionOfType(MailSendException.class).isThrownBy(
        () -> mailService.sendSimpleMail(mail));

  }

  @Test
  public void shouldThrowExceptionWhenSendEmailWithHtmlTemplateHasNotWellFormedRecipientAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("as@cc.ch")
                                            .subject("Hello")
                                            .to(singletonList("123as   }$§d!!0"))
                                            .build();

    //when
    assertThatExceptionOfType(MailSendException.class).isThrownBy(
        () -> mailService.sendSimpleMail(mail));

  }

  @Test
  public void shouldThrowExceptionWhenSendEmailWithHtmlTemplateHasNotWellFormedFromAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("123as   }$§d!!0")
                                            .subject("Hello")
                                            .to(singletonList("123as   }$§d!!0"))
                                            .build();

    //when
    assertThatExceptionOfType(MailSendException.class).isThrownBy(
        () -> mailService.sendSimpleMail(mail));
  }

  private MailNotification createMail() {
    return MailNotification.builder()
                           .subject("Hello world")
                           .from("no-reply@aa.com")
                           .to(singletonList("info@aa.com"))
                           .content("Ciao Ragazzi.")
                           .build();
  }
}
