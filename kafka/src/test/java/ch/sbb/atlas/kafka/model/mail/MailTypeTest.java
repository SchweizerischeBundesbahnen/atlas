package ch.sbb.atlas.kafka.model.mail;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MailTypeTest {

  @Test
  void testEnumValues() {
    MailType[] expectedValues = {
        MailType.ATLAS_STANDARD,
        MailType.TU_IMPORT,
        MailType.SCHEDULING_ERROR_NOTIFICATION,
        MailType.UPDATE_GEOLOCATION_ERROR_NOTIFICATION,
        MailType.UPDATE_GEOLOCATION_SUCCESS_NOTIFICATION,
        MailType.EXPORT_SERVICE_POINT_ERROR_NOTIFICATION,
        MailType.WORKFLOW_NOTIFICATION,
        MailType.START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION,
        MailType.START_STOP_POINT_WORKFLOW_CC_NOTIFICATION,
        MailType.REJECT_STOP_POINT_WORKFLOW_NOTIFICATION,
        MailType.STOP_POINT_WORKFLOW_PINCODE_NOTIFICATION,
        MailType.APPROVED_STOP_POINT_WORKFLOW_NOTIFICATION,
        MailType.CANCEL_STOP_POINT_WORKFLOW_NOTIFICATION,
        MailType.STOP_POINT_WORKFLOW_RESTART_NOTIFICATION,
        MailType.STOP_POINT_WORKFLOW_RESTART_CC_NOTIFICATION
    };
    assertThat(MailType.values()).isEqualTo(expectedValues);
  }

}
