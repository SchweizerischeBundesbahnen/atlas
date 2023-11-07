package ch.sbb.prm.directory.validation;

import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.StopPointRecordingVariantException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class StopPointValidationService {

  public void validateStopPointRecordingVariants(StopPointVersion stopPointVersion) {
    boolean isReduced = PrmMeansOfTransportHelper.isReduced(stopPointVersion.getMeansOfTransport());
    Map<String, String> errorConstraintMap = new HashMap<>();
    List<Field> notForReducedFields = Arrays.stream(stopPointVersion.getClass().getDeclaredFields())
        .filter(field -> field.isAnnotationPresent(NotForReduced.class)).toList();
    if (isReduced) {
      notForReducedFields.forEach(field -> {
        try {
          field.setAccessible(true);
          Object fieldWithValue = field.get(stopPointVersion);
          if (fieldWithValue != null) {
            errorConstraintMap.put(field.getName(), "Must be null for Reduced StopPoint");
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      });
    }
    else {
      notForReducedFields.forEach(field -> {
        try {
          field.setAccessible(true);
          Object fieldWithValue = field.get(stopPointVersion);
          if (fieldWithValue == null) {
            errorConstraintMap.put(field.getName(), "Must not be null for Completed StopPoint");
          }
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      });
    }

    if (!errorConstraintMap.isEmpty()) {
      throw new StopPointRecordingVariantException(stopPointVersion, errorConstraintMap);
    }

  }

}
