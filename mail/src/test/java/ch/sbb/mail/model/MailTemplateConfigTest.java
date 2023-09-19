package ch.sbb.mail.model;

import static ch.sbb.mail.model.MailTemplateConfig.getMailTemplateConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import ch.sbb.atlas.kafka.model.mail.MailType;
import org.junit.jupiter.api.Test;

class MailTemplateConfigTest {

  @Test
   void shouldThrowExceptionWhenMailTypeIsNull() {
    //when
    assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
        () -> getMailTemplateConfig(null));

  }

  @Test
   void shouldReturnAtlasStandardTemplate() {
    //when
    MailTemplateConfig result = getMailTemplateConfig(MailType.ATLAS_STANDARD);
    //then
    assertThat(result).isEqualTo(MailTemplateConfig.ATLAS_STANDARD_TEMPLATE);
  }

  @Test
   void shouldReturnTuImportTemplate() {
    //when
    MailTemplateConfig result = getMailTemplateConfig(MailType.TU_IMPORT);
    //then
    assertThat(result).isEqualTo(MailTemplateConfig.IMPORT_TU_TEMPLATE);
  }

  @Test
   void shouldReturnSchedulingErrorNotificationTemplate() {
    //when
    MailTemplateConfig result = getMailTemplateConfig(MailType.SCHEDULING_ERROR_NOTIFICATION);
    //then
    assertThat(result).isEqualTo(MailTemplateConfig.SCHEDULING_ERROR_NOTIFICATION_TEMPLATE);
  }

  @Test
   void shouldReturnWorkflowNotification() {
    //when
    MailTemplateConfig result = getMailTemplateConfig(MailType.WORKFLOW_NOTIFICATION);
    //then
    assertThat(result).isEqualTo(MailTemplateConfig.WORKFLOW_NOTIFICATION_TEMPLATE);
  }

}