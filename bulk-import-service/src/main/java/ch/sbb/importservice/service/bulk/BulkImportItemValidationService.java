package ch.sbb.importservice.service.bulk;

import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.Validatable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BulkImportItemValidationService {

  public static <T extends Validatable> void validateAll(List<BulkImportUpdateContainer<T>> items) {
    items.stream()
        .filter(Objects::nonNull)
        .filter(i -> i.getObject() != null)
        .forEach(BulkImportItemValidationService::validate);
  }

  public static <T extends Validatable> void validate(BulkImportUpdateContainer<T> item) {
    T object = item.getObject();
    List<BulkImportError> validationErrors = object.validate();
    if (!validationErrors.isEmpty()) {
      storeInfoInLogEntry(item, validationErrors);
    }
  }

  private static <T extends Validatable> void storeInfoInLogEntry(BulkImportUpdateContainer<T> item,
      List<BulkImportError> validationErrors) {
    if (item.getBulkImportLogEntry() == null) {
      item.setBulkImportLogEntry(BulkImportLogEntry.builder()
          .lineNumber(item.getLineNumber())
          .status(BulkImportStatus.DATA_VALIDATION_ERROR)
          .errors(new ArrayList<>(validationErrors))
          .build());
    } else {
      item.getBulkImportLogEntry().getErrors().addAll(validationErrors);
    }
  }
}
