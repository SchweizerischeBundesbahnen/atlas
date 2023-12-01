package ch.sbb.prm.directory.validation;

import ch.sbb.prm.directory.exception.RecordingVariantException;
import ch.sbb.prm.directory.validation.annotation.PrmVariant;
import ch.sbb.prm.directory.validation.annotation.RecordingVariant;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public abstract class RecordableVariantsValidationService<T extends VariantsReducedCompleteRecordable> {

  public static final String MUST_BE_NULL_ERROR_MSG = "Must be null for Reduced Object";
  public static final String NOT_ALLOWED_FIELD_FOR_COMPLETE_VARIANT_ERROR_MSG = "Must be null for Completed Object";
  public static final String MUST_NOT_BE_NULL_ERROR_MSG = "Must not be null for Completed Object. At least a default value "
      + "is mandatory";

  protected abstract String getObjectName();

  public void validateRecordingVariants(T version, boolean isReduced) {

    Map<String, String> errorConstraintMap = new HashMap<>();
    List<Field> prmVariantFields = Arrays.stream(version.getClass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(PrmVariant.class)).toList();
    if (isReduced) {
      prmVariantFields.forEach(
          field -> checkReducedVariantDoesNotProvideCompleteProperties(version, errorConstraintMap, field));
    } else {
      prmVariantFields.forEach(field -> checkCompleteVariantProvideAllMandatoryProperties(version, errorConstraintMap, field));
    }

    if (!errorConstraintMap.isEmpty()) {
      throw new RecordingVariantException(errorConstraintMap, getObjectName(), version.getSloid(), isReduced);
    }

  }

  private void checkCompleteVariantProvideAllMandatoryProperties(T version, Map<String, String> errorConstraintMap, Field field) {
    try {
      boolean nullable = field.getAnnotation(PrmVariant.class).nullable();
      RecordingVariant recordingVariant = field.getAnnotation(PrmVariant.class).variant();
      field.setAccessible(true);
      Object value = field.get(version);
      if (!nullable && value == null) {
        errorConstraintMap.put(field.getName(), MUST_NOT_BE_NULL_ERROR_MSG);
      }
      if(value instanceof Collection<?> collection){
        if (!collection.isEmpty() && RecordingVariant.REDUCED == recordingVariant) {
          errorConstraintMap.put(field.getName(), NOT_ALLOWED_FIELD_FOR_COMPLETE_VARIANT_ERROR_MSG);
        }
      }else if (value != null && RecordingVariant.REDUCED == recordingVariant) {
        errorConstraintMap.put(field.getName(), NOT_ALLOWED_FIELD_FOR_COMPLETE_VARIANT_ERROR_MSG);
      }
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  private void checkReducedVariantDoesNotProvideCompleteProperties(T version, Map<String, String> errorConstraintMap,
      Field field) {
    try {
      RecordingVariant recordingVariant = field.getAnnotation(PrmVariant.class).variant();
      field.setAccessible(true);
      Object value = field.get(version);
      if (value != null && RecordingVariant.COMPLETE == recordingVariant) {
        errorConstraintMap.put(field.getName(), MUST_BE_NULL_ERROR_MSG);
      }
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

}
