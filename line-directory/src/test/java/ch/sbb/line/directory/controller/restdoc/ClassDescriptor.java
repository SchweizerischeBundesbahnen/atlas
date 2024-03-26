package ch.sbb.line.directory.controller.restdoc;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Data
public class ClassDescriptor {

  public static final String LINE_SEPERATOR = "\n";

  private final List<FieldDescriptor> fields = new ArrayList<>();

  public ClassDescriptor(Class<?> clazz) {
    this.fields.addAll(
        getAllFields(clazz).stream().filter(field -> !Modifier.isStatic(field.getModifiers()))
            .map(ClassDescriptor::buildFieldDescriptions).flatMap(Collection::stream)
            .toList());
  }

  private static List<Field> getAllFields(Class<?> clazz) {
    List<Field> fields = new ArrayList<>(List.of(clazz.getDeclaredFields()));
    if (fields.isEmpty()) {
      return Collections.emptyList();
    } else {
      fields.addAll(getAllFields(clazz.getSuperclass()));
    }
    return fields;
  }

  private static List<FieldDescriptor> buildFieldDescriptions(Field field) {
    List<FieldDescriptor> fieldDescriptions = new ArrayList<>();
    FieldDescriptor currentField = FieldDescriptor.builder()
        .fieldName(field.getName())
        .type(getType(field))
        .optional(!isMandatory(field))
        .description(String.join(LINE_SEPERATOR + LINE_SEPERATOR, buildDescriptionForField(field)))
        .build();
    fieldDescriptions.add(currentField);

    if (currentField.getType().equals("Array[Object]")) {
      Class<?> collectionType = getCollectionClass(field);
      ClassDescriptor collectionTypeDescription = new ClassDescriptor(collectionType);
      List<FieldDescriptor> collectionFields = collectionTypeDescription.getFields();
      collectionFields.forEach(
          collectionField -> collectionField.setFieldName(currentField.getFieldName() + "[]." + collectionField.getFieldName()));
      fieldDescriptions.addAll(collectionFields);
    }
    if (currentField.getType().equals("Object")) {
      Class<?> subType = field.getType();
      ClassDescriptor subTypeDescription = new ClassDescriptor(subType);
      List<FieldDescriptor> collectionFields = subTypeDescription.getFields();
      collectionFields.forEach(
          collectionField -> collectionField.setFieldName(currentField.getFieldName() + "." + collectionField.getFieldName()));
      fieldDescriptions.addAll(collectionFields);
    }
    return fieldDescriptions;
  }

  @Data
  @Builder
  public static class FieldDescriptor {

    private String fieldName;
    private String type;
    private boolean optional;
    private String description;

  }

  private static String getType(Field field) {
    if (field.getType().isEnum()) {
      return "String";
    }
    if (isCollection(field.getType())) {
      Class<?> collectionType = getCollectionClass(field);
      if (collectionType.isEnum()) {
        return "Array[String]";
      }
      return "Array[%s]".formatted(getSimpleName(collectionType.getName()));
    }
    return getSimpleName(field.getGenericType().getTypeName());
  }

  private static boolean isCollection(Class<?> clazz) {
    return Set.of(Set.class.getName(), List.class.getName()).contains(clazz.getName());
  }

  private static Class<?> getCollectionClass(Field field) {
    String className = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
    try {
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException(e);
    }
  }

  private static String getSimpleName(String canonicalName) {
    return switch (canonicalName) {
      case "java.lang.Byte", "java.lang.Short", "java.lang.Long", "java.lang.Integer", "java.math.BigInteger" -> "Integer";
      case "java.lang.Float", "java.lang.Double", "java.math.BigDecimal" -> "Decimal";
      case "java.lang.Character", "java.lang.String", "java.time.Instant", "java.util.Locale",
          "java.time.LocalDate", "java.time.LocalDateTime" -> "String";
      case "java.lang.Boolean" -> "Boolean";
      default -> "Object";
    };
  }

  private static boolean isMandatory(Field field) {
    if (field.getType().isPrimitive()) {
      return true;
    }
    return Arrays.stream(field.getAnnotations()).anyMatch(i ->
        i.annotationType().equals(NotNull.class) ||
            i.annotationType().equals(NotBlank.class) ||
            i.annotationType().equals(NotEmpty.class));
  }

  private static List<String> buildDescriptionForField(Field field) {
    List<String> descriptionParts = new ArrayList<>();
    if (field.isAnnotationPresent(Schema.class)) {
      descriptionParts.add(buildDescriptionForSchema(field));
    }
    if (field.getType().isEnum()) {
      descriptionParts.add(buildDescriptionForEnum(field));
    }
    if (isCollection(field.getType()) && getCollectionClass(field).isEnum()) {
      descriptionParts.add(buildDescriptionForEnumType((Class<Enum<?>>) getCollectionClass(field)));
    }
    if (field.isAnnotationPresent(Pattern.class)) {
      descriptionParts.add(buildDescriptionForPattern(field));
    }
    if (field.isAnnotationPresent(Size.class)) {
      descriptionParts.add(buildDescriptionForSize(field));
    }
    return descriptionParts;
  }

  private static String buildDescriptionForSchema(Field field) {
    Schema schemaAnnotation = field.getAnnotation(Schema.class);
    String exampleDescription =
        StringUtils.isNotBlank(schemaAnnotation.example()) ?
            LINE_SEPERATOR + LINE_SEPERATOR + "Example value: " + schemaAnnotation.example() :
            "";
    return schemaAnnotation.description() + exampleDescription;
  }

  private static String buildDescriptionForEnum(Field field) {
    return buildDescriptionForEnumType((Class<Enum<?>>) field.getType());
  }

  private static String buildDescriptionForEnumType(Class<Enum<?>> enumType) {
    String enumValueDescription = Arrays.stream(enumType.getEnumConstants()).map(Enum::name).map("\"%s\""::formatted).collect(
        Collectors.joining(","));
    return "Must be one of [" + enumValueDescription + "]";
  }

  private static String buildDescriptionForPattern(Field field) {
    Pattern patternAnnotation = field.getAnnotation(Pattern.class);
    String regexp = patternAnnotation.regexp();
    return "Must conform to regex " + regexp;
  }

  private static String buildDescriptionForSize(Field field) {
    Size sizeAnnotation = field.getAnnotation(Size.class);
    return "Length must be between " + sizeAnnotation.min() + " and " + sizeAnnotation.max();
  }

}
