package ch.sbb.atlas.user.administration.security.redact;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.user.administration.security.aspect.ParameterInfoReflectionResolver;
import ch.sbb.atlas.user.administration.security.aspect.ParameterInfoReflectionResolver.ParameterInfo;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Aspect
@Component
@RequiredArgsConstructor
public class RedactDecider {

  private final BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;

  boolean shouldRedact(ProceedingJoinPoint joinPoint, Object resultObject) {
    Optional<ParameterInfo<RedactBySboid>> redactBySboidParameterInfo = ParameterInfoReflectionResolver.byAnnotation(joinPoint,
        RedactBySboid.class);
    if (redactBySboidParameterInfo.isPresent()) {
      ApplicationType application = redactBySboidParameterInfo.get().getAnnotation().application();
      String sboid = redactBySboidParameterInfo.get().getValueAsString();

      return shouldRedactBySboid(sboid, application);
    } else {
      Optional<Field> redactBySboidField = Arrays.stream(resultObject.getClass().getDeclaredFields())
          .filter(i -> i.isAnnotationPresent(RedactBySboid.class)).findFirst();

      if (redactBySboidField.isPresent()) {
        ReflectionUtils.makeAccessible(redactBySboidField.get());
        String sboidField = (String) ReflectionUtils.getField(redactBySboidField.get(), resultObject);
        return shouldRedactBySboid(sboidField, redactBySboidField.get().getAnnotation(RedactBySboid.class).application());
      } else {
        return UserService.hasUnauthorizedRole();
      }
    }
  }

  private boolean shouldRedactBySboid(String sboid, ApplicationType application) {
    boolean hasPermission = businessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisation(sboid,
        application);
    boolean isUnauthorized = UserService.hasUnauthorizedRole();
    return !hasPermission || isUnauthorized;
  }
}