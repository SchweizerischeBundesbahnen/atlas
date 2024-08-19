package ch.sbb.importservice.model;

import ch.sbb.atlas.imports.bulk.ImportType;
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

}
