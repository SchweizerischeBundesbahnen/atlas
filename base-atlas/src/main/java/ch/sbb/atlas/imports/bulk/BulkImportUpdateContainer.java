package ch.sbb.atlas.imports.bulk;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Data
public class BulkImportUpdateContainer<T> implements BulkImportContainer {

  private int lineNumber;

  @Valid
  private T object;

  @Builder.Default
  private List<String> attributesToNull = new ArrayList<>();

  private BulkImportLogEntry bulkImportLogEntry;

  @JsonIgnore
  public boolean hasDataValidationErrors() {
    return bulkImportLogEntry != null && bulkImportLogEntry.getStatus() == BulkImportStatus.DATA_VALIDATION_ERROR;
  }

}
