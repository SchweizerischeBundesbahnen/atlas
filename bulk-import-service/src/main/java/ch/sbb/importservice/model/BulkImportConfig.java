package ch.sbb.importservice.model;

import static ch.sbb.importservice.service.bulk.template.BulkImportTemplateGenerator.CSV_EXTENSION;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@RequiredArgsConstructor
@FieldNameConstants
public class BulkImportConfig {

  private final ApplicationType application;
  private final BusinessObjectType objectType;
  private final ImportType importType;

  public String getTemplateFileName() {
    return application.name() + "_" + objectType.name() + "_" + importType.name() + CSV_EXTENSION;
  }

}
