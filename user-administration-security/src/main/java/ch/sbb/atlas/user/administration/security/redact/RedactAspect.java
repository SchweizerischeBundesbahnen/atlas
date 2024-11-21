package ch.sbb.atlas.user.administration.security.redact;

import ch.sbb.atlas.versioning.convert.ReflectionHelper;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
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

  private final RedactDecider redactDecider;

  @Around("@annotation(ch.sbb.atlas.user.administration.security.redact.Redacted)")
  public Object redactSensitiveData(ProceedingJoinPoint joinPoint) throws Throwable {
    Object resultObject = joinPoint.proceed();

    boolean shouldRedact = redactDecider.shouldRedact(joinPoint, resultObject);
    if (!shouldRedact) {
      return resultObject;
    }

    return redactResult(resultObject);
  }

  Object redactResult(Object resultObject) {
    if (resultObject instanceof Page<?> page) {
      List<Object> redactedPage = page.getContent().stream().map(pageItem -> new ObjectRedactor(pageItem).accept()).toList();
      return new PageImpl<>(redactedPage, page.getPageable(), page.getTotalElements());
    } else {
      return new ObjectRedactor(resultObject).accept();
    }
  }

  @AllArgsConstructor
  private static class ObjectRedactor {

    private Object object;

    public Object accept() {
      if (object == null) {
        return null;
      }
      if (object.getClass().isAnnotationPresent(Redacted.class)) {

        object = ReflectionHelper.copyObjectViaBuilder(object);

        for (Field field : object.getClass().getDeclaredFields()) {
          if (field.isAnnotationPresent(Redacted.class) && field.getType().isAnnotationPresent(Redacted.class)) {
            ReflectionUtils.makeAccessible(field);
            Object fieldObject = ReflectionUtils.getField(field, object);
            Object redactObject = new ObjectRedactor(fieldObject).accept();
            ReflectionUtils.setField(field, object, redactObject);
          } else if (field.isAnnotationPresent(Redacted.class)) {
            new FieldRedactor(field, object).accept();
          }
        }
        return object;
      } else {
        throw new IllegalStateException("Class not configured for redact. Class name=" + object.getClass().getName());
      }
    }

  }

  private static class FieldRedactor {

    private final Field field;
    private final Object currentFieldValue;
    private final Object object;
    private final boolean showFirstChar;

    private boolean redactPerformed = false;

    public FieldRedactor(Field field, Object object) {
      ReflectionUtils.makeAccessible(field);
      this.currentFieldValue = ReflectionUtils.getField(field, object);
      this.field = field;
      this.object = object;

      Redacted redacted = Objects.requireNonNull(field.getAnnotation(Redacted.class),
          "Redacted field must be annotated with @Redacted");
      this.showFirstChar = redacted.showFirstChar();
    }

    public void accept() {
      if (field.getGenericType().equals(String.class)) {
        performRedact(StringRedactor.redactString((String) currentFieldValue, showFirstChar));
      }

      if (field.getGenericType() instanceof ParameterizedType parameterizedType) {
        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];

        if (actualTypeArgument.equals(String.class)) {
          if (currentFieldValue instanceof List<?> list) {
            List<String> redactedList = list.stream().map(i -> StringRedactor.redactString((String) i, showFirstChar))
                .toList();
            performRedact(redactedList);
          }
        } else {
          if (currentFieldValue instanceof Set<?> set) {
            Set<Object> redactedSet = set.stream().map(i -> new ObjectRedactor(i).accept()).collect(Collectors.toSet());
            performRedact(redactedSet);
          }
        }
      }

      if (!redactPerformed) {
        throw new IllegalStateException("Field not redacted. Field type not supported. Field name=" + field.getName());
      }
    }

    private void performRedact(Object newValue) {
      ReflectionUtils.setField(field, object, newValue);
      redactPerformed = true;
    }
  }

}
