package ch.sbb.importservice.service.bulk;

import ch.sbb.atlas.imports.bulk.BulkImportErrors;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportError;
import ch.sbb.atlas.imports.bulk.BulkImportLogEntry.BulkImportStatus;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.Validatable;
import ch.sbb.atlas.imports.bulk.Validatable.UniqueField;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BulkImportValidationService {

  public static <T extends Validatable<T>> void validateUniqueness(List<BulkImportUpdateContainer<T>> models) {
    Set<DuplicatedValue<T>> duplicatedValues = getDuplicatedValues(models);

    models.stream()
        .filter(i -> i.getObject() != null)
        .forEach(model -> duplicatedValues.stream()
            .filter(i -> i.getObjectPredicate().test(model.getObject()))
            .findFirst()
            .ifPresent(duplicatedObject -> addDuplicatedValueLogEntry(model, duplicatedObject)));
  }

  private static <T extends Validatable<T>> void addDuplicatedValueLogEntry(BulkImportUpdateContainer<T> model,
      DuplicatedValue<T> duplicatedObject) {
    BulkImportError bulkImportError = BulkImportErrors.duplicatedValue(duplicatedObject.getFieldName(),
        String.valueOf(duplicatedObject.getValue()));
    if (model.getBulkImportLogEntry() == null) {
      model.setBulkImportLogEntry(BulkImportLogEntry.builder()
          .lineNumber(model.getLineNumber())
          .status(BulkImportStatus.DATA_VALIDATION_ERROR)
          .errors(List.of(bulkImportError))
          .build());
    } else {
      model.getBulkImportLogEntry().getErrors().add(bulkImportError);
    }
  }

  private static <T extends Validatable<T>> Set<DuplicatedValue<T>> getDuplicatedValues(
      List<BulkImportUpdateContainer<T>> models) {
    Map<String, Set<Object>> uniqueElements = new HashMap<>();
    Set<DuplicatedValue<T>> duplicatedValues = new HashSet<>();

    models.stream()
        .map(BulkImportUpdateContainer::getObject)
        .filter(Objects::nonNull).forEach(object -> {
          List<UniqueField<T>> uniqueFields = object.uniqueFields();
          uniqueFields.forEach(uniqueField -> {
            Object fieldValue = uniqueField.getFieldValueExtractor().apply(object);
            if (fieldValue != null && !uniqueElements.computeIfAbsent(uniqueField.getField(), k -> new HashSet<>()).add(fieldValue)) {
              duplicatedValues.add(new DuplicatedValue<>(uniqueField.getField(), fieldValue,
                  i -> Objects.equals(uniqueField.getFieldValueExtractor().apply(i), fieldValue)));
            }
          });
        });

    return duplicatedValues;
  }

  @Getter
  @RequiredArgsConstructor
  private static class DuplicatedValue<U> {

    private final String fieldName;
    private final Object value;
    private final Predicate<U> objectPredicate;
  }
}
