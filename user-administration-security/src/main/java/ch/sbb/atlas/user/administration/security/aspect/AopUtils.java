package ch.sbb.atlas.user.administration.security.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

@UtilityClass
public class AopUtils {

  public static Optional<Object> resolveParameterValueByAnnotation(ProceedingJoinPoint joinPoint,
      Class<? extends Annotation> annotation) {
    return resolveParameterInfoByAnnotation(joinPoint, annotation).map(ParameterInfo::getValue);
  }

  public static <T extends Annotation> Optional<ParameterInfo<T>> resolveParameterInfoByAnnotation(ProceedingJoinPoint joinPoint,
      Class<T> annotation) {
    try {
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      Method method = signature.getMethod();

      Class<?>[] parameterTypes = method.getParameterTypes();

      Annotation[][] parameterAnnotations = joinPoint.getTarget()
          .getClass()
          .getMethod(method.getName(), parameterTypes)
          .getParameterAnnotations();

      for (int i = 0; i < joinPoint.getArgs().length; i++) {
        Annotation[] annotations = parameterAnnotations[i];

        for (Annotation a : annotations) {
          if (a.annotationType() == annotation) {
            return Optional.of(ParameterInfo.<T>builder()
                .value(joinPoint.getArgs()[i])
                .annotation(annotation.cast(a))
                .build());
          }
        }
      }
    } catch (NoSuchMethodException e) {
      return Optional.empty();
    }
    return Optional.empty();
  }

  @Builder
  @Data
  public static class ParameterInfo<T extends Annotation> {

    private Object value;
    private T annotation;

    public String getValueAsString() {
      return (String) value;
    }
  }

}
