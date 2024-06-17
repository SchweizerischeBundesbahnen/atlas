package ch.sbb.mail.model;

import static ch.sbb.atlas.kafka.model.mail.MailType.ATLAS_STANDARD;
import static ch.sbb.atlas.kafka.model.mail.MailType.EXPORT_SERVICE_POINT_ERROR_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.IMPORT_SERVICE_POINT_ERROR_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.IMPORT_SERVICE_POINT_SUCCESS_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.REJECT_STOP_POINT_WORKFLOW_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.SCHEDULING_ERROR_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.START_STOP_POINT_WORKFLOW_CC_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION;
import static ch.sbb.atlas.kafka.model.mail.MailType.TU_IMPORT;
import static ch.sbb.atlas.kafka.model.mail.MailType.WORKFLOW_NOTIFICATION;

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
  IMPORT_SERVCICE_POINT_ERROR_NOTIFICATION_TEMPLATE("import-error-notification", null, null, false, false, true),
  IMPORT_SERVCICE_POINT_SUCCESS_NOTIFICATION_TEMPLATE("import-success-notification", null, null, false, false,
      true),
  EXPORT_SERVCICE_POINT_ERROR_NOTIFICATION_TEMPLATE("export-service-point-error-notification", null, null, false, false,
      true),
  WORKFLOW_NOTIFICATION_TEMPLATE("workflow_notification", null, null, true, false, true),
  START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION_TEMPLATE("start_stop_point_workflow_examinant_notification", null, null, true,
      false, true),
  START_STOP_POINT_WORKFLOW_CC_NOTIFICATION_TEMPLATE("start_stop_point_workflow_cc_notification", null, null, true, false, true),
  REJECT_STOP_POINT_WORKFLOW_NOTIFICATION_TEMPLATE("reject_stop_point_workflow_notification", null, null, true, false, true);

  private final String template;
  private final String subject;
  private final String[] to;
  private final boolean from;
  private final boolean content;
  private final boolean templateProperties;

  private static final Map<MailType, MailTemplateConfig> MAIL_TYPE_TEMPLATE_CONFIG = new EnumMap<>(MailType.class);

  static {
    MAIL_TYPE_TEMPLATE_CONFIG.put(ATLAS_STANDARD, ATLAS_STANDARD_TEMPLATE);
    MAIL_TYPE_TEMPLATE_CONFIG.put(TU_IMPORT, IMPORT_TU_TEMPLATE);
    MAIL_TYPE_TEMPLATE_CONFIG.put(SCHEDULING_ERROR_NOTIFICATION, SCHEDULING_ERROR_NOTIFICATION_TEMPLATE);
    MAIL_TYPE_TEMPLATE_CONFIG.put(WORKFLOW_NOTIFICATION, WORKFLOW_NOTIFICATION_TEMPLATE);
    MAIL_TYPE_TEMPLATE_CONFIG.put(IMPORT_SERVICE_POINT_ERROR_NOTIFICATION, IMPORT_SERVCICE_POINT_ERROR_NOTIFICATION_TEMPLATE);
    MAIL_TYPE_TEMPLATE_CONFIG.put(IMPORT_SERVICE_POINT_SUCCESS_NOTIFICATION, IMPORT_SERVCICE_POINT_SUCCESS_NOTIFICATION_TEMPLATE);
    MAIL_TYPE_TEMPLATE_CONFIG.put(EXPORT_SERVICE_POINT_ERROR_NOTIFICATION, EXPORT_SERVCICE_POINT_ERROR_NOTIFICATION_TEMPLATE);
    MAIL_TYPE_TEMPLATE_CONFIG.put(START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION,
        START_STOP_POINT_WORKFLOW_EXAMINANT_NOTIFICATION_TEMPLATE);
    MAIL_TYPE_TEMPLATE_CONFIG.put(START_STOP_POINT_WORKFLOW_CC_NOTIFICATION, START_STOP_POINT_WORKFLOW_CC_NOTIFICATION_TEMPLATE);
    MAIL_TYPE_TEMPLATE_CONFIG.put(REJECT_STOP_POINT_WORKFLOW_NOTIFICATION, REJECT_STOP_POINT_WORKFLOW_NOTIFICATION_TEMPLATE);
  }

  public static MailTemplateConfig getMailTemplateConfig(MailType mailType) {
    if (mailType == null) {
      throw new IllegalArgumentException("You have to provide a mailType");
    }
    MailTemplateConfig mailTemplateConfig = MAIL_TYPE_TEMPLATE_CONFIG.get(mailType);
    if (mailTemplateConfig != null) {
      return mailTemplateConfig;
    }
    throw new IllegalArgumentException("No configuration provided for: " + mailType.name());
  }

}
