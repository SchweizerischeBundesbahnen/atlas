package ch.sbb.importservice.service.bulk;

import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.Validatable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BulkImportValidationService {

  public static <T extends Validatable> void validateUniqueness(List<BulkImportUpdateContainer<T>> models,
      Function<T, Object> uniqueFieldExtractor, String fieldName) {
    List<Object> duplicateValues = getDuplicatedValues(models, uniqueFieldExtractor);

    models.stream()
        .filter(i -> i.getObject() != null)
        .filter(model -> duplicateValues.contains(uniqueFieldExtractor.apply(model.getObject())))
        .forEach(duplicatedLine -> {
          String duplicatedValue = String.valueOf(uniqueFieldExtractor.apply(duplicatedLine.getObject()));
          if (duplicatedLine.getBulkImportLogEntry() == null) {
            duplicatedLine.setBulkImportLogEntry(BulkImportLogEntry.builder()
                .lineNumber(duplicatedLine.getLineNumber())
                .status(BulkImportStatus.DATA_VALIDATION_ERROR)
                .errors(List.of(BulkImportErrors.duplicatedValue(fieldName, duplicatedValue)))
                .build());
          } else {
            duplicatedLine.getBulkImportLogEntry().getErrors().add(BulkImportErrors.duplicatedValue(fieldName, duplicatedValue));
          }
        });
  }

  private static <T extends Validatable> List<Object> getDuplicatedValues(List<BulkImportUpdateContainer<T>> models,
      Function<T, Object> uniqueFieldExtractor) {
    Set<Object> uniqueElements = new HashSet<>();
    return models.stream()
        .map(BulkImportUpdateContainer::getObject)
        .filter(Objects::nonNull)
        .map(uniqueFieldExtractor)
        .filter(Objects::nonNull)
        .filter(i -> !uniqueElements.add(i))
        .toList();
  }
}
