package ch.sbb.mail.service;

import ch.sbb.mail.exception.MailSendException;
import ch.sbb.mail.model.MailNotification;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.String.valueOf;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class EmailServiceIntegrationTest {

  @Rule
  public SmtpServerRule smtpServerRule = new SmtpServerRule(2525);

  @Autowired
  private MailService mailService;

  @Test
  public void shouldSendSimpleMail() throws MessagingException, IOException {
    //given
    MailNotification mail = createMail();

    //when
    mailService.sendSimpleMail(mail);

    //then
    MimeMessage[] receivedMessages = smtpServerRule.getMessages();
    assertThat(receivedMessages.length, is(1));

    MimeMessage current = receivedMessages[0];

    assertThat(mail.getSubject(), is(current.getSubject()));
    assertThat(mail.getTo(), hasItem(current.getAllRecipients()[0].toString()));
    assertThat(valueOf(current.getContent()).contains(mail.getContent()), is(true));

  }

  public void shouldThrowExceptionWhenSmtpServerIsDown() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("as@cc.ch")
                                            .subject("Hello")
                                            .to(singletonList("as@cc.ch"))
                                            .build();
    smtpServerRule.after();

    //when
    mailService.sendSimpleMail(mail);

  }

  @Test(expected = MailSendException.class)
  public void shouldThrowExceptionWhenSendSimpleEmailHasNotRecipientAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("aa@bb.ch")
                                            .subject("Hello")
                                            .to(new ArrayList<>())
                                            .build();

    //when
    mailService.sendSimpleMail(mail);

  }

  @Test(expected = MailSendException.class)
  public void shouldThrowExceptionWhenSendSimpleEmailHasNotWellFormedRecipientAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("as@cc.ch")
                                            .subject("Hello")
                                            .to(singletonList("123as   }$§d!!0"))
                                            .build();

    //when
    mailService.sendSimpleMail(mail);

  }

  @Test(expected = MailSendException.class)
  public void shouldThrowExceptionWhenSendSimpleEmailHasNotWellFormedFromAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .subject("Hello")
                                            .to(singletonList("123as   }$§d!!0"))
                                            .build();

    //when
    mailService.sendSimpleMail(mail);

  }

  @Test
  public void shouldSendEmailWithHtmlTemplate() throws MessagingException, IOException {
    //given
    MailNotification mail = createMail();

    //when
    mailService.sendEmailWithHtmlTemplate(mail);

    //then
    MimeMessage[] receivedMessages = smtpServerRule.getMessages();
    assertThat(receivedMessages.length, is(1));

    MimeMessage current = receivedMessages[0];

    assertThat(mail.getSubject(), is(current.getSubject()));
    assertThat(mail.getTo(), hasItem(current.getAllRecipients()[0].toString()));
    assertThat(current.getContentType(), is("text/html;charset=UTF-8"));
    assertThat(valueOf(current.getContent()).contains(mail.getContent()), is(true));
    assertThat(current.getContent().toString().contains("<span>Ciao Ragazzi.</span>"), is(true));

  }

  @Test(expected = MailSendException.class)
  public void shouldThrowExceptionWhenSendEmailWithHtmlTemplateHasNotRecipientAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("aa@bb.ch")
                                            .subject("Hello")
                                            .to(new ArrayList<>())
                                            .build();

    //when
    mailService.sendEmailWithHtmlTemplate(mail);

  }

  @Test(expected = MailSendException.class)
  public void shouldThrowExceptionWhenSendEmailWithHtmlTemplateHasNotWellFormedRecipientAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("as@cc.ch")
                                            .subject("Hello")
                                            .to(singletonList("123as   }$§d!!0"))
                                            .build();

    //when
    mailService.sendEmailWithHtmlTemplate(mail);

  }

  @Test(expected = MailSendException.class)
  public void shouldThrowExceptionWhenSendEmailWithHtmlTemplateHasNotWellFormedFromAddress() {
    //given
    MailNotification mail = MailNotification.builder()
                                            .content("Ciao ragazzi")
                                            .from("123as   }$§d!!0")
                                            .subject("Hello")
                                            .to(singletonList("123as   }$§d!!0"))
                                            .build();

    //when
    mailService.sendEmailWithHtmlTemplate(mail);

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
