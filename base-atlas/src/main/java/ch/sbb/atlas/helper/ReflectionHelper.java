package ch.sbb.atlas.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.util.ReflectionUtils;

@UtilityClass
public final class ReflectionHelper {

  public static Field getFieldAccessible(Class<?> clazz, String fieldName)
      throws NoSuchFieldException {
    List<Field> fields = getAllFieldsAccessible(clazz);
    return fields.stream().filter(i -> i.getName().equals(fieldName)).findFirst().orElseThrow(
        () -> new NoSuchFieldException(fieldName));
  }

  public static Object getFieldValue(Object object, String fieldName) {
    try {
      Field field = ReflectionHelper.getFieldAccessible(object.getClass(), fieldName);
      return field.get(object);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static List<Field> getAllFieldsAccessible(Class<?> clazz) {
    List<Field> fields = getAllFields(clazz);
    fields.forEach(ReflectionUtils::makeAccessible);
    return fields;
  }

  private static List<Field> getAllFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<>();
    while (clazz.getSuperclass() != null) {
      fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
      clazz = clazz.getSuperclass();
    }
    return fields;
  }

  public static <T> T copyObjectViaBuilder(T object) {
    try {
      Method toBuilder = object.getClass().getDeclaredMethod("toBuilder");
      ReflectionUtils.makeAccessible(toBuilder);
      Object builder = toBuilder.invoke(object);
      Method build = builder.getClass().getMethod("build");
      ReflectionUtils.makeAccessible(build);
      return (T) build.invoke(builder);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Could not invoke .toBuilder().build() for Object copy on " + object.getClass().getSimpleName(), e);
    }
  }
}
