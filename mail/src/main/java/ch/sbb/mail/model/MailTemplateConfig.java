package ch.sbb.mail.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(enumAsRef = true)
public enum MailTemplateConfig {

  STANDARD_HTML_TEMPLATE("mail-html-template",null,true,true,false),
  IMPORT_TU_TEMPLATE("import-tu","[ATLAS] Import Transportunternehmen",false,false,true);

  private final String template;
  private final String subject;
  private final boolean from;
  private final boolean content;
  private final boolean templateProperties;

  public static MailTemplateConfig getMailTemplateConfig(MailType mailType){
    if(MailType.TU_IMPORT == mailType){
      return IMPORT_TU_TEMPLATE;
    }
    throw new IllegalArgumentException("No configuration provided for {}" + mailType.name());
  }

}
