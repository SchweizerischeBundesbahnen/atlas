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
  void shouldReturnExportServicePointNotificationErrorNotification() {
    //when
    MailTemplateConfig result = getMailTemplateConfig(MailType.EXPORT_SERVICE_POINT_ERROR_NOTIFICATION);
    //then
    assertThat(result).isEqualTo(MailTemplateConfig.EXPORT_SERVCICE_POINT_ERROR_NOTIFICATION_TEMPLATE);
  }

  @Test
  void shouldReturnWorkflowNotification() {
    //when
    MailTemplateConfig result = getMailTemplateConfig(MailType.WORKFLOW_NOTIFICATION);
    //then
    assertThat(result).isEqualTo(MailTemplateConfig.WORKFLOW_NOTIFICATION_TEMPLATE);
  }

  @Test
  void shouldReturnStartStopPointWorkflowExaminantNotification() {
    //when
    MailTemplateConfig result = getMailTemplateConfig(MailType.START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION);
    //then
    assertThat(result).isEqualTo(MailTemplateConfig.START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION_TEMPLATE);
  }

  @Test
  void shouldReturnStartStopPointWorkflowCCNotification() {
    //when
    MailTemplateConfig result = getMailTemplateConfig(MailType.START_STOP_POINT_WORKFLOW_CC_NOTIFICATION);
    //then
    assertThat(result).isEqualTo(MailTemplateConfig.START_STOP_POINT_WORKFLOW_CC_NOTIFICATION_TEMPLATE);
  }

  @Test
  void shouldReturnRejectStopPointWorkflowNotification() {
    //when
    MailTemplateConfig result = getMailTemplateConfig(MailType.REJECT_STOP_POINT_WORKFLOW_NOTIFICATION);
    //then
    assertThat(result).isEqualTo(MailTemplateConfig.REJECT_STOP_POINT_WORKFLOW_NOTIFICATION_TEMPLATE);
  }

}