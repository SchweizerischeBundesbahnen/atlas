package ch.sbb.atlas.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.UtilityClass;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

@UtilityClass
public class ParameterInfoReflectionResolver {

  public static Optional<Object> valueByAnnotation(ProceedingJoinPoint joinPoint, Class<? extends Annotation> annotation) {
    return byAnnotation(joinPoint, annotation).map(ParameterInfo::getValue);
  }

  public static <T extends Annotation> Optional<ParameterInfo<T>> byAnnotation(ProceedingJoinPoint joinPoint,
      Class<T> annotation) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    return getParameterInfo(method, Arrays.asList(joinPoint.getArgs()), annotation);
  }

  private static <T extends Annotation> Optional<ParameterInfo<T>> getParameterInfo(Method method, List<Object> methodArgs,
      Class<T> annotation) {
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();

    for (int i = 0; i < method.getParameterCount(); i++) {
      Annotation[] annotations = parameterAnnotations[i];

      for (Annotation a : annotations) {
        if (a.annotationType() == annotation) {
          return Optional.of(ParameterInfo.<T>builder().value(methodArgs.get(i)).annotation(annotation.cast(a)).build());
        }
      }
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
