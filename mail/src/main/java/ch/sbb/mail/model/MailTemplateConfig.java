package ch.sbb.mail.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Schema(enumAsRef = true)
public enum MailTemplateConfig {

  ATLAS_STANDARD_TEMPLATE("atlas-basic-html-template",null,null,true,true,false),
  IMPORT_TU_TEMPLATE("import-tu","[ATLAS] Import Transportunternehmen",new String[]{"didok@sbb.ch"},false,false,true);

  private final String template;
  private final String subject;
  private final String[] to;
  private final boolean from;
  private final boolean content;
  private final boolean templateProperties;

  public static MailTemplateConfig getMailTemplateConfig(MailType mailType){
    if(MailType.ATLAS_STANDARD == mailType){
      return ATLAS_STANDARD_TEMPLATE;
    }
    if(MailType.TU_IMPORT == mailType){
      return IMPORT_TU_TEMPLATE;
    }
    throw new IllegalArgumentException("No configuration provided for: " + mailType.name());
  }

}
