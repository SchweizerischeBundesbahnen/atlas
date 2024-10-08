package ch.sbb.importservice.model;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BulkImportResult {

  private ImportType importType;
  private BusinessObjectType businessObjectType;
  private String creator;
  private LocalDateTime creationDate;
  private String inNameOf;
  private Long nbOfSuccess;
  private Long nbOfInfo;
  private Long nbOfError;
  private List<BulkImportLogEntry> logEntries;

}
