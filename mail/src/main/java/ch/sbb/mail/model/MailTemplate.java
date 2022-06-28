package ch.sbb.mail.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(enumAsRef = true)
public enum MailTemplate {

  STANDARD_HTML_TEMPLATE("mail-html-template"),
  IMPORT_TU_TEMPLATE("import-tu");

  private final String template;
}
