package ch.sbb.importservice.service.bulk;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.Validatable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkImportItemValidationService {

  public <T extends Validatable> void validateAll(List<BulkImportUpdateContainer<T>> items) {
    items.stream()
        .filter(Objects::nonNull)
        .filter(i -> i.getObject() != null)
        .forEach(this::validate);
  }

  public <T extends Validatable> void validate(BulkImportUpdateContainer<T> item) {
    T object = item.getObject();
    List<BulkImportError> validationErrors = object.validate();
    if (!validationErrors.isEmpty()) {
      if (item.getBulkImportLogEntry() != null) {
        item.getBulkImportLogEntry().getErrors().addAll(validationErrors);
      } else {
        item.setBulkImportLogEntry(BulkImportLogEntry.builder()
            .lineNumber(item.getLineNumber())
            .status(BulkImportStatus.DATA_VALIDATION_ERROR)
            .errors(new ArrayList<>(validationErrors))
            .build());
      }
    }
  }
}
