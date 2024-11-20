package ch.sbb.workflow.aop;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.service.UserService;
import ch.sbb.atlas.user.administration.security.aspect.ParameterInfoReflectionResolver;
import ch.sbb.atlas.user.administration.security.aspect.ParameterInfoReflectionResolver.ParameterInfo;
import ch.sbb.atlas.user.administration.security.service.BusinessOrganisationBasedUserAdministrationService;
import ch.sbb.atlas.versioning.convert.ReflectionHelper;
import ch.sbb.workflow.helper.StringHelper;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Aspect
@Component
@RequiredArgsConstructor
public class RedactAspect {

  private final BusinessOrganisationBasedUserAdministrationService businessOrganisationBasedUserAdministrationService;

  @Around("@annotation(ch.sbb.workflow.aop.Redacted)")
  public Object redactSensitiveData(final ProceedingJoinPoint joinPoint) throws Throwable {
    return redact(joinPoint);
  }

  Object redact(ProceedingJoinPoint joinPoint) throws Throwable {
    Object resultObject = joinPoint.proceed();

    boolean redact = shouldRedact(joinPoint, resultObject);

    if (!redact) {
      return resultObject;
    }

    if (resultObject instanceof Page<?> page) {
      List<Object> redactedPage = page.getContent().stream().map(this::redactObject).toList();
      return new PageImpl<>(redactedPage, page.getPageable(), page.getTotalElements());
    } else {
      return this.redactObject(resultObject);
    }
  }

  private boolean shouldRedact(ProceedingJoinPoint joinPoint, Object resultObject) {
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

  Object redactObject(Object object) {
    if (object == null) {
      return null;
    }
    if (object.getClass().isAnnotationPresent(Redacted.class)) {

      object = ReflectionHelper.copyObjectViaBuilder(object);

      for (Field field : object.getClass().getDeclaredFields()) {
        if (field.isAnnotationPresent(Redacted.class) && field.getType().isAnnotationPresent(Redacted.class)) {
          Object fieldObject = ReflectionUtils.getField(field, object);
          Object redactObject = redactObject(fieldObject);
          ReflectionUtils.setField(field, object, redactObject);
        } else if (field.isAnnotationPresent(Redacted.class)) {
          redactField(field, object);
        }
      }
      return object;
    } else {
      throw new IllegalStateException("Class not configured for redact");
    }
  }


  void redactField(Field field, Object object) {
    ReflectionUtils.makeAccessible(field);
    Object currentFieldValue = ReflectionUtils.getField(field, object);
    Redacted redacted = field.getAnnotation(Redacted.class);

    if (currentFieldValue instanceof String stringValue) {
      ReflectionUtils.setField(field, object, StringHelper.redactString(stringValue, redacted.showFirstChar()));
    }

    if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
      Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];

      if (actualTypeArgument.equals(String.class)) {
        if (currentFieldValue instanceof List<?> list) {
          List<String> redactedList = list.stream().map(i -> StringHelper.redactString((String) i, redacted.showFirstChar()))
              .toList();
          ReflectionUtils.setField(field, object, redactedList);
        }
      } else {
        if (currentFieldValue instanceof Set<?> set) {
          Set<Object> redactedSet = set.stream().map(this::redactObject).collect(Collectors.toSet());
          ReflectionUtils.setField(field, object, redactedSet);
        }
      }
    }
  }

  private boolean shouldRedactBySboid(String sboid, ApplicationType application) {
    boolean hasPermission = businessOrganisationBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisation(sboid,application);
    boolean isUnauthorized = UserService.hasUnauthorizedRole();
    return !hasPermission || isUnauthorized;
  }

}
