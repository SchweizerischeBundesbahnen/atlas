package ch.sbb.atlas.auto.rest.doc.descriptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.core.MethodParameter;

@Getter
@ToString
@EqualsAndHashCode
public class ParameterWrapper {

  private final String name;
  private final Class<?> type;
  private final boolean collection;
  private final Set<Annotation> annotations;
  private boolean optional;

  public ParameterWrapper(Field field) {
    this.name = field.getName();
    this.collection = isCollection(field.getType());
    if (collection) {
      this.type = getGenericClass(field);
    } else {
      if (field.getType().equals(Optional.class)) {
        this.optional = true;
        this.type = getGenericClass(field);
      } else {
        this.type = field.getType();
      }
    }
    this.annotations = Set.of(field.getAnnotations());
  }

  public ParameterWrapper(MethodParameter field) {
    this.name = field.getParameterName();
    this.collection = isCollection(field.getParameterType());
    if (collection) {
      this.type = getGenericClass(field);
    } else {
      if (field.getParameterType().equals(Optional.class)) {
        this.optional = true;
        this.type = getGenericClass(field);
      } else {
        this.type = field.getParameterType();
      }
    }
    this.annotations = Set.of(field.getParameterAnnotations());
  }

  public boolean isEnum() {
    return getType().isEnum();
  }

  public boolean isPrimitive() {
    return getType().isPrimitive();
  }

  public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
    return getAnnotation(annotationClass) != null;
  }

  public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
    return annotationClass.cast(
        annotations.stream().filter(i -> i.annotationType().equals(annotationClass)).findFirst().orElse(null));
  }

  private static boolean isCollection(Class<?> clazz) {
    return Set.of(Set.class.getName(), List.class.getName()).contains(clazz.getName());
  }

  private Class<?> getGenericClass(Field field) {
    return getGenericClass((ParameterizedType) field.getGenericType());
  }

  private Class<?> getGenericClass(MethodParameter field) {
    return getGenericClass((ParameterizedType) field.getGenericParameterType());
  }

  private Class<?> getGenericClass(ParameterizedType type) {
    String className = type.getActualTypeArguments()[0].getTypeName();
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      return Object.class;
    }
  }

}
