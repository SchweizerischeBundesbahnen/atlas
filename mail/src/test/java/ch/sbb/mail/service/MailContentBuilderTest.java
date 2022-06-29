package ch.sbb.mail.service;


import static ch.sbb.mail.model.MailTemplateConfig.getMailTemplateConfig;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.mail.model.MailNotification;
import ch.sbb.mail.model.MailType;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.thymeleaf.TemplateEngine;

public class MailContentBuilderTest {

  @Mock
  private TemplateEngine templateEngine;

  private MailContentBuilder mailContentBuilder;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    mailContentBuilder = new MailContentBuilder(templateEngine);
  }

  @Test
  public void shouldReturnToFromMailTemplateConfig() {
    //given
    MailNotification mailNotification = MailNotification.builder().build();

    //when
    String[] result = mailContentBuilder.getTo(getMailTemplateConfig(MailType.TU_IMPORT),
        mailNotification);

    //then
    assertThat(result).isEqualTo(getMailTemplateConfig(MailType.TU_IMPORT).getTo());
  }

  @Test
  public void shouldReturnToFromMailNotification() {
    //given
    MailNotification mailNotification =
        MailNotification.builder()
                        .to(singletonList("asd@b.ch"))
                        .build();

    //when
    String[] result = mailContentBuilder.getTo(getMailTemplateConfig(MailType.TU_IMPORT),
        mailNotification);

    //then
    assertThat(result).hasSize(1);
    assertThat(result[0]).isEqualTo("asd@b.ch");
  }

  @Test
  public void shouldThrowExceptionWhenToFromMailNotificationAndMailTemplateCofigIsNull() {
    //given
    MailNotification mailNotification =
        MailNotification.builder()
                        .build();

    //when
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
        () -> mailContentBuilder.getTo(getMailTemplateConfig(MailType.ATLAS_STANDARD),
            mailNotification));

  }

}