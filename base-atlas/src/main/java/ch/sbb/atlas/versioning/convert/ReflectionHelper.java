package ch.sbb.atlas.versioning.convert;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ReflectionHelper {

  public static Field getFieldAccessible(Class<?> clazz, String fieldName)
      throws NoSuchFieldException {
    List<Field> fields = getAllFieldsAccessible(clazz);
    return fields.stream().filter(i -> i.getName().equals(fieldName)).findFirst().orElseThrow(
        () -> new NoSuchFieldException(fieldName));
  }

  public static List<Field> getAllFieldsAccessible(Class<?> clazz) {
    List<Field> fields = getAllFields(clazz);
    fields.forEach(i -> i.setAccessible(true));
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

  public static Object copyObjectViaBuilder(Object object) {
    try {
      Method toBuilder = object.getClass().getDeclaredMethod("toBuilder");
      toBuilder.setAccessible(true);
      Object builder = toBuilder.invoke(object);
      Method build = builder.getClass().getMethod("build");
      build.setAccessible(true);
      return build.invoke(builder);
    } catch (Exception e) {
      throw new IllegalStateException(
          "Could not invoke .toBuilder().build() for Object copy on " + object.getClass().getSimpleName(), e);
    }
  }
}
