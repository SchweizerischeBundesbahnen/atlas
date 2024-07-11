package ch.sbb.atlas.kafka.model.mail;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class MailTypeTest {

  @Test
  public void testEnumValues() {
    for (MailType mailType : MailType.values()) {
      assertThat(mailType).isNotNull();
    }
  }

  @Test
  public void testSpecificEnumValues() {
    assertThat(MailType.ATLAS_STANDARD).isEqualTo(MailType.valueOf("ATLAS_STANDARD"));
    assertThat(MailType.TU_IMPORT).isEqualTo(MailType.valueOf("TU_IMPORT"));
    assertThat(MailType.SCHEDULING_ERROR_NOTIFICATION).isEqualTo(MailType.valueOf("SCHEDULING_ERROR_NOTIFICATION"));
    assertThat(MailType.IMPORT_SERVICE_POINT_ERROR_NOTIFICATION).isEqualTo(MailType.valueOf("IMPORT_SERVICE_POINT_ERROR_NOTIFICATION"));
    assertThat(MailType.IMPORT_SERVICE_POINT_SUCCESS_NOTIFICATION).isEqualTo(MailType.valueOf("IMPORT_SERVICE_POINT_SUCCESS_NOTIFICATION"));
    assertThat(MailType.EXPORT_SERVICE_POINT_ERROR_NOTIFICATION).isEqualTo(MailType.valueOf("EXPORT_SERVICE_POINT_ERROR_NOTIFICATION"));
    assertThat(MailType.WORKFLOW_NOTIFICATION).isEqualTo(MailType.valueOf("WORKFLOW_NOTIFICATION"));
    assertThat(MailType.START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION).isEqualTo(MailType.valueOf("START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION"));
    assertThat(MailType.START_STOP_POINT_WORKFLOW_CC_NOTIFICATION).isEqualTo(MailType.valueOf("START_STOP_POINT_WORKFLOW_CC_NOTIFICATION"));
    assertThat(MailType.REJECT_STOP_POINT_WORKFLOW_NOTIFICATION).isEqualTo(MailType.valueOf("REJECT_STOP_POINT_WORKFLOW_NOTIFICATION"));
    assertThat(MailType.STOP_POINT_WORKFLOW_PINCODE_NOTIFICATION).isEqualTo(MailType.valueOf("STOP_POINT_WORKFLOW_PINCODE_NOTIFICATION"));
    assertThat(MailType.APPROVED_STOP_POINT_WORKFLOW_NOTIFICATION).isEqualTo(MailType.valueOf("APPROVED_STOP_POINT_WORKFLOW_NOTIFICATION"));
    assertThat(MailType.CANCEL_STOP_POINT_WORKFLOW_NOTIFICATION).isEqualTo(MailType.valueOf("CANCEL_STOP_POINT_WORKFLOW_NOTIFICATION"));
    assertThat(MailType.STOP_POINT_WORKFLOW_RESTART_NOTIFICATION).isEqualTo(MailType.valueOf("STOP_POINT_WORKFLOW_RESTART_NOTIFICATION"));
    assertThat(MailType.STOP_POINT_WORKFLOW_RESTART_CC_NOTIFICATION).isEqualTo(MailType.valueOf("STOP_POINT_WORKFLOW_RESTART_CC_NOTIFICATION"));
  }

}
