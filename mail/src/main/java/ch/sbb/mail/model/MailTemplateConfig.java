package ch.sbb.mail.model;

import ch.sbb.atlas.kafka.model.mail.MailType;
import io.swagger.v3.oas.annotations.media.Schema;
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
  WORKFLOW_NOTIFICATION_TEMPLATE("workflow_notification", null, null, true, false, true);

  private final String template;
  private final String subject;
  private final String[] to;
  private final boolean from;
  private final boolean content;
  private final boolean templateProperties;

  public static MailTemplateConfig getMailTemplateConfig(MailType mailType) {
    if (mailType == null) {
      throw new IllegalArgumentException("You have to provide a mailType");
    }
    if (MailType.ATLAS_STANDARD == mailType) {
      return ATLAS_STANDARD_TEMPLATE;
    }
    if (MailType.TU_IMPORT == mailType) {
      return IMPORT_TU_TEMPLATE;
    }
    if (MailType.SCHEDULING_ERROR_NOTIFICATION == mailType) {
      return SCHEDULING_ERROR_NOTIFICATION_TEMPLATE;
    }
    if (MailType.WORKFLOW_NOTIFICATION == mailType) {
      return WORKFLOW_NOTIFICATION_TEMPLATE;
    }
    if (MailType.IMPORT_SERVICE_POINT_ERROR_NOTIFICATION == mailType) {
      return IMPORT_SERVCICE_POINT_ERROR_NOTIFICATION_TEMPLATE;
    }
    if (MailType.IMPORT_SERVICE_POINT_SUCCESS_NOTIFICATION == mailType) {
      return IMPORT_SERVCICE_POINT_SUCCESS_NOTIFICATION_TEMPLATE;
    }
    if (MailType.EXPORT_SERVICE_POINT_ERROR_NOTIFICATION == mailType) {
      return EXPORT_SERVCICE_POINT_ERROR_NOTIFICATION_TEMPLATE;
    }
    throw new IllegalArgumentException("No configuration provided for: " + mailType.name());
  }

}
