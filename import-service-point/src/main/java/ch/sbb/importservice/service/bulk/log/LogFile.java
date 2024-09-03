package ch.sbb.importservice.service.bulk.log;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogFile {

  @Builder.Default
  private List<BulkImportLogEntry> logEntries = new ArrayList<>();

}
