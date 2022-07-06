package ch.sbb.mail.service;


import static ch.sbb.mail.model.MailTemplateConfig.getMailTemplateConfig;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.model.mail.MailNotification;
import ch.sbb.mail.model.MailTemplateConfig;
import ch.sbb.atlas.model.mail.MailType;
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

  @Test
  public void shouldGetSubjectFromMailNotification(){
    //given
    MailNotification mailNotification =
        MailNotification.builder()
                        .to(singletonList("asd@b.ch"))
            .subject("Soggetto")
                        .build();

    //when
    String result = mailContentBuilder.getSubject(MailTemplateConfig.ATLAS_STANDARD_TEMPLATE,
        mailNotification);

    //then
    assertThat(result).isEqualTo("Soggetto");
  }

  @Test
  public void shouldGetSubjectFromMailTemplateConfig(){
    //given
    MailNotification mailNotification =
        MailNotification.builder()
                        .to(singletonList("asd@b.ch"))
            .subject("Soggetto")
                        .build();

    //when
    String result = mailContentBuilder.getSubject(MailTemplateConfig.IMPORT_TU_TEMPLATE,
        mailNotification);

    //then
    assertThat(result).isEqualTo(MailTemplateConfig.IMPORT_TU_TEMPLATE.getSubject());
  }

  @Test
  public void shouldThrowExceptionWhenNoSubjectIsDefined(){
    //given
    MailNotification mailNotification =
        MailNotification.builder()
                        .to(singletonList("asd@b.ch"))
                        .build();

    //when
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
        () ->  mailContentBuilder.getSubject(MailTemplateConfig.ATLAS_STANDARD_TEMPLATE,
        mailNotification));

  }
  @Test
  public void shouldGetFromFromMailNotification(){
    //given
    MailNotification mailNotification =
        MailNotification.builder()
                        .from("asd@b.ch")
            .subject("Soggetto")
                        .build();

    //when
    String result = mailContentBuilder.getFrom(MailTemplateConfig.ATLAS_STANDARD_TEMPLATE,
        mailNotification);

    //then
    assertThat(result).isEqualTo("asd@b.ch");
  }

  @Test
  public void shouldGetFromFromMailTemplateConfig(){
    //given
    MailNotification mailNotification =
        MailNotification.builder()
                        .to(singletonList("asd@b.ch"))
            .subject("Soggetto")
                        .build();

    //when
    String result = mailContentBuilder.getFrom(MailTemplateConfig.ATLAS_STANDARD_TEMPLATE,
        mailNotification);

    //then
    assertThat(result).isEqualTo("TechSupport-ATLAS@sbb.ch");
  }

}