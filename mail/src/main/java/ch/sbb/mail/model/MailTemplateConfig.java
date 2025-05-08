package ch.sbb.mail.model;

import static ch.sbb.atlas.kafka.model.mail.MailType.APPROVED_STOP_POINT_WORKFLOW_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.ATLAS_STANDARD;
import static ch.sbb.atlas.kafka.model.mail.MailType.BULK_IMPORT_RESULT_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.CANCEL_STOP_POINT_WORKFLOW_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.EXPORT_SERVICE_POINT_ERROR_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.LINE_APPROVED_WORKFLOW_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.LINE_REJECTED_WORKFLOW_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.LINE_STARTED_WORKFLOW_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.REJECT_STOP_POINT_WORKFLOW_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.SCHEDULING_ERROR_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.START_STOP_POINT_WORKFLOW_CC_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.START_TERMINATION_STOP_POINT_WORKFLOW_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.STOP_POINT_WORKFLOW_PINCODE_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.STOP_POINT_WORKFLOW_RESTART_CC_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.STOP_POINT_WORKFLOW_RESTART_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.TU_IMPORT;
import static ch.sbb.atlas.kafka.model.mail.MailType.UPDATE_GEOLOCATION_ERROR_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.UPDATE_GEOLOCATION_SUCCESS_NOTIFICATION;

import ch.sbb.atlas.kafka.model.mail.MailType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.EnumMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(enumAsRef = true)
public enum MailTemplateConfig {

  ATLAS_STANDARD_TEMPLATE("atlas-basic-html-template", null, null, true, true, false),
  IMPORT_TU_TEMPLATE("import-tu", "Import Transportunternehmen", new String[]{"didok@sbb.ch"}, false, false, true),
  SCHEDULING_ERROR_NOTIFICATION_TEMPLATE("scheduling-error-notification", null, null, false, false, true),
  UPDATE_GEOLOCATION_ERROR_NOTIFICATION_TEMPLATE("update-geolocation-error-notification", null, null, false, false, true),
  UPDATE_GEOLOCATION_SUCCESS_NOTIFICATION_TEMPLATE("update-geolocation-success-notification", null, null, false, false,
      true),
  EXPORT_SERVCICE_POINT_ERROR_NOTIFICATION_TEMPLATE("export-service-point-error-notification", null, null, false, false,
      true),
  WORKFLOW_NOTIFICATION_TEMPLATE("workflow_notification", null, null, true, false, true),
  LINE_STARTED_WORKFLOW_NOTIFICATION_TEMPLATE("line_started_workflow_notification", null, null, true, false, true),
  LINE_APPROVED_WORKFLOW_NOTIFICATION_TEMPLATE("line_approved_workflow_notification", null, null, true, false, true),
  LINE_REJECTED_WORKFLOW_NOTIFICATION_TEMPLATE("line_rejected_workflow_notification", null, null, true, false, true),
  START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION_TEMPLATE("start_stop_point_workflow_examinant_notification", null, null, true,
      false, true),
  START_STOP_POINT_WORKFLOW_CC_NOTIFICATION_TEMPLATE("start_stop_point_workflow_cc_notification", null, null, true, false, true),
  REJECT_STOP_POINT_WORKFLOW_NOTIFICATION_TEMPLATE("reject_stop_point_workflow_notification", null, null, true, false, true),
  STOP_POINT_WORKFLOW_PINCODE_NOTIFICATION_TEMPLATE("stop_point_workflow_pincode_notification", null, null, true, false, true),
  APPROVED_STOP_POINT_WORKFLOW_NOTIFICATION_TEMPLATE("approved_stop_point_workflow_notification", null, null, true, false, true),
  CANCEL_STOP_POINT_WORKFLOW_NOTIFICATION_TEMPLATE("reject_and_cancel_stop_point_workflow_notification", null, null, true, false,
      true),
  STOP_POINT_WORKFLOW_RESTART_NOTIFICATION_TEMPLATE("stop_point_workflow_restart_notification", null, null, true, false, true),
  STOP_POINT_WORKFLOW_RESTART_CC_NOTIFICATION_TEMPLATE("stop_point_workflow_restart_cc_notification", null, null, true, false,
      true),
  BULK_IMPORT_RESULT_TEMPLATE("bulk-import-result-template", null, null, true, false, true),
  START_TERMINATION_STOP_POINT_WORKFLOW_NOTIFICATION_TEMPLATE("termination_stop_point_workflow_notification", null, null, true,
      false, true),

  ;

  private final String template;
  private final String subject;
  private final String[] to;
  private final boolean from;
  private final boolean content;
  private final boolean templateProperties;

  private static final Map<MailType, MailTemplateConfig> CONFIG = new EnumMap<>(MailType.class);

  static {
    CONFIG.put(ATLAS_STANDARD, ATLAS_STANDARD_TEMPLATE);
    CONFIG.put(TU_IMPORT, IMPORT_TU_TEMPLATE);
    CONFIG.put(SCHEDULING_ERROR_NOTIFICATION, SCHEDULING_ERROR_NOTIFICATION_TEMPLATE);
    CONFIG.put(LINE_STARTED_WORKFLOW_NOTIFICATION, LINE_STARTED_WORKFLOW_NOTIFICATION_TEMPLATE);
    CONFIG.put(LINE_APPROVED_WORKFLOW_NOTIFICATION, LINE_APPROVED_WORKFLOW_NOTIFICATION_TEMPLATE);
    CONFIG.put(LINE_REJECTED_WORKFLOW_NOTIFICATION, LINE_REJECTED_WORKFLOW_NOTIFICATION_TEMPLATE);
    CONFIG.put(UPDATE_GEOLOCATION_ERROR_NOTIFICATION, UPDATE_GEOLOCATION_ERROR_NOTIFICATION_TEMPLATE);
    CONFIG.put(UPDATE_GEOLOCATION_SUCCESS_NOTIFICATION, UPDATE_GEOLOCATION_SUCCESS_NOTIFICATION_TEMPLATE);
    CONFIG.put(EXPORT_SERVICE_POINT_ERROR_NOTIFICATION, EXPORT_SERVCICE_POINT_ERROR_NOTIFICATION_TEMPLATE);
    CONFIG.put(START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION, START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION_TEMPLATE);
    CONFIG.put(START_STOP_POINT_WORKFLOW_CC_NOTIFICATION, START_STOP_POINT_WORKFLOW_CC_NOTIFICATION_TEMPLATE);
    CONFIG.put(REJECT_STOP_POINT_WORKFLOW_NOTIFICATION, REJECT_STOP_POINT_WORKFLOW_NOTIFICATION_TEMPLATE);
    CONFIG.put(STOP_POINT_WORKFLOW_PINCODE_NOTIFICATION, STOP_POINT_WORKFLOW_PINCODE_NOTIFICATION_TEMPLATE);
    CONFIG.put(APPROVED_STOP_POINT_WORKFLOW_NOTIFICATION, APPROVED_STOP_POINT_WORKFLOW_NOTIFICATION_TEMPLATE);
    CONFIG.put(CANCEL_STOP_POINT_WORKFLOW_NOTIFICATION, CANCEL_STOP_POINT_WORKFLOW_NOTIFICATION_TEMPLATE);
    CONFIG.put(STOP_POINT_WORKFLOW_RESTART_NOTIFICATION, STOP_POINT_WORKFLOW_RESTART_NOTIFICATION_TEMPLATE);
    CONFIG.put(STOP_POINT_WORKFLOW_RESTART_CC_NOTIFICATION, STOP_POINT_WORKFLOW_RESTART_CC_NOTIFICATION_TEMPLATE);
    CONFIG.put(BULK_IMPORT_RESULT_NOTIFICATION, BULK_IMPORT_RESULT_TEMPLATE);
    CONFIG.put(START_TERMINATION_STOP_POINT_WORKFLOW_NOTIFICATION, START_TERMINATION_STOP_POINT_WORKFLOW_NOTIFICATION_TEMPLATE);
  }

  public static MailTemplateConfig getMailTemplateConfig(MailType mailType) {
    if (mailType == null) {
      throw new IllegalArgumentException("You have to provide a mailType");
    }
    MailTemplateConfig mailTemplateConfig = CONFIG.get(mailType);
    if (mailTemplateConfig != null) {
      return mailTemplateConfig;
    }
    throw new IllegalArgumentException("No configuration provided for: " + mailType.name());
  }

}
