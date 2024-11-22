package ch.sbb.atlas.redact;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.reflection.ParameterInfoReflectionResolver;
import ch.sbb.atlas.reflection.ParameterInfoReflectionResolver.ParameterInfo;
import ch.sbb.atlas.service.UserService;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.ReflectionUtils;

@RequiredArgsConstructor
public class RedactDecider {

  private final Optional<RedactBySboidDecider> redactBySboidDecider;

  boolean shouldRedact(ProceedingJoinPoint joinPoint, Object resultObject) {
    if (redactBySboidDecider.isPresent()) {
      Optional<ParameterInfo<RedactBySboid>> redactBySboidParameterInfo = ParameterInfoReflectionResolver.byAnnotation(joinPoint,
          RedactBySboid.class);
      if (redactBySboidParameterInfo.isPresent()) {
        return shouldRedactBySboidParameter(redactBySboidParameterInfo.get());
      }
      Optional<Field> redactBySboidField = Arrays.stream(resultObject.getClass().getDeclaredFields())
          .filter(i -> i.isAnnotationPresent(RedactBySboid.class)).findFirst();

      if (redactBySboidField.isPresent()) {
        return shouldRedactBySboidField(resultObject, redactBySboidField.get());
      }
    }
    return UserService.hasUnauthorizedRole();
  }

  private boolean shouldRedactBySboidParameter(ParameterInfo<RedactBySboid> redactBySboidParameterInfo) {
    ApplicationType application = redactBySboidParameterInfo.getAnnotation().application();
    String sboid = redactBySboidParameterInfo.getValueAsString();

    return shouldRedactBySboid(sboid, application);
  }

  private boolean shouldRedactBySboidField(Object resultObject, Field redactBySboidField) {
    ReflectionUtils.makeAccessible(redactBySboidField);
    String sboidField = (String) ReflectionUtils.getField(redactBySboidField, resultObject);

    return shouldRedactBySboid(sboidField, redactBySboidField.getAnnotation(RedactBySboid.class).application());
  }

  private boolean shouldRedactBySboid(String sboid, ApplicationType application) {
    boolean hasPermission = redactBySboidDecider.orElseThrow().hasUserPermissionsForBusinessOrganisation(sboid,
        application);
    boolean isUnauthorized = UserService.hasUnauthorizedRole();
    return !hasPermission || isUnauthorized;
  }
}