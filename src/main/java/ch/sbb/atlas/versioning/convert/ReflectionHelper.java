package ch.sbb.atlas.versioning.convert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ReflectionHelper {

  private ReflectionHelper() {
    throw new IllegalStateException("Utility class");
  }

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
}
