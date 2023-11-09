package ch.sbb.prm.directory.validation;

import ch.sbb.prm.directory.exception.RecordingVariantException;
import ch.sbb.prm.directory.validation.annotation.NotForReducedVariant;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public abstract class RecordableVariantsValidationService<T extends VariantsReducedCompleteRecordable> {

  public static final String MUST_BE_NULL_ERROR_MSG = "Must be null for Reduced Object";
  public static final String MUST_NOT_BE_NULL_ERROR_MSG = "Must not be null for Completed Object. At least a default value "
      + "is mandatory";

  protected abstract String getObjectName();

  public void validateRecordingVariants(T version, boolean isReduced) {

    Map<String, String> errorConstraintMap = new HashMap<>();
    List<Field> notForReducedFields = Arrays.stream(version.getClass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(NotForReducedVariant.class)).toList();
    if (isReduced) {
      notForReducedFields.forEach(field -> {
        try {
          field.setAccessible(true);
          Object value = field.get(version);
          if (value != null) {
            errorConstraintMap.put(field.getName(), MUST_BE_NULL_ERROR_MSG);
          }
        } catch (IllegalAccessException e) {
          throw new IllegalStateException(e);
        }
      });
    } else {
      notForReducedFields.forEach(field -> {
        try {
          boolean nullable = field.getAnnotation(NotForReducedVariant.class).nullable();
          field.setAccessible(true);
          Object value = field.get(version);
          if (!nullable && value == null) {
            errorConstraintMap.put(field.getName(), MUST_NOT_BE_NULL_ERROR_MSG);
          }
        } catch (IllegalAccessException e) {
          throw new IllegalStateException(e);
        }
      });
    }

    if (!errorConstraintMap.isEmpty()) {
      throw new RecordingVariantException(errorConstraintMap, getObjectName(), version.getSloid(), isReduced);
    }

  }

}
