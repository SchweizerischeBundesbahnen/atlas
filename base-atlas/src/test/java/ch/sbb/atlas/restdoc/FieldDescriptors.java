package ch.sbb.atlas.restdoc;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Data
public class FieldDescriptors {

  public static final String LINE_SEPERATOR = "\n";

  private final List<FieldDescriptor> fields = new ArrayList<>();

  public FieldDescriptors(Class<?> clazz) {
    this.fields.addAll(
        getAllFields(clazz).stream()
            .filter(field -> !Modifier.isStatic(field.getModifiers()))
            .map(ParameterWrapper::new)
            .map(FieldDescriptors::buildFieldDescriptions)
            .flatMap(Collection::stream)
            .toList());
  }

  public FieldDescriptors(List<MethodParameter> methodParameters) {
    this.fields.addAll(methodParameters.stream()
        .map(ParameterWrapper::new)
        .map(FieldDescriptors::buildFieldDescriptions)
        .flatMap(Collection::stream)
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

  private static List<FieldDescriptor> buildFieldDescriptions(ParameterWrapper field) {
    List<FieldDescriptor> fieldDescriptions = new ArrayList<>();
    FieldDescriptor currentField = FieldDescriptor.builder()
        .fieldName(field.getName())
        .type(getType(field))
        .optional(!isMandatory(field))
        .description(String.join(LINE_SEPERATOR + LINE_SEPERATOR, buildDescriptionForField(field)))
        .build();
    fieldDescriptions.add(currentField);

    if (currentField.getType().equals("Array[Object]")) {
      Class<?> collectionType = field.getType();
      FieldDescriptors collectionTypeDescription = new FieldDescriptors(collectionType);
      List<FieldDescriptor> collectionFields = collectionTypeDescription.getFields();
      collectionFields.forEach(
          collectionField -> collectionField.setFieldName(currentField.getFieldName() + "[]." + collectionField.getFieldName()));
      fieldDescriptions.addAll(collectionFields);
    }
    if (currentField.getType().equals("Object")) {
      Class<?> subType = field.getType();
      FieldDescriptors subTypeDescription = new FieldDescriptors(subType);
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
    @Builder.Default
    private String description = "";

  }

  private static String getType(ParameterWrapper field) {
    if (field.isCollection() && field.isEnum()) {
      return "Array[String]";
    }
    if (field.isCollection()) {
      return "Array[%s]".formatted(getSimpleName(field.getType().getName()));
    }
    if (field.isEnum()) {
      return "String";
    }
    return getSimpleName(field.getType().getName());
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

  private static boolean isMandatory(ParameterWrapper field) {
    if (field.isPrimitive()) {
      return true;
    }
    if (field.isOptional()) {
      return false;
    }
    RequestParam requestParam = field.getAnnotation(RequestParam.class);
    if (requestParam != null && !requestParam.required()) {
      return false;
    }
    return field.getAnnotations().stream().anyMatch(i ->
        i.annotationType().equals(NotNull.class) ||
            i.annotationType().equals(NotBlank.class) ||
            i.annotationType().equals(NotEmpty.class));
  }

  private static List<String> buildDescriptionForField(ParameterWrapper field) {
    List<String> descriptionParts = new ArrayList<>();
    if (field.isAnnotationPresent(Schema.class)) {
      descriptionParts.add(buildDescriptionForSchema(field));
    }
    if (field.isCollection() && field.isEnum()) {
      descriptionParts.add(buildDescriptionForEnumType((Class<Enum<?>>) field.getType()));
    } else if (field.isEnum()) {
      descriptionParts.add(buildDescriptionForEnum(field));
    }
    if (field.isAnnotationPresent(Pattern.class)) {
      descriptionParts.add(buildDescriptionForPattern(field));
    }
    if (field.isAnnotationPresent(Size.class)) {
      descriptionParts.add(buildDescriptionForSize(field));
    }
    return descriptionParts;
  }

  private static String buildDescriptionForSchema(ParameterWrapper field) {
    Schema schemaAnnotation = field.getAnnotation(Schema.class);
    String exampleDescription =
        StringUtils.isNotBlank(schemaAnnotation.example()) ?
            LINE_SEPERATOR + LINE_SEPERATOR + "Example value: " + schemaAnnotation.example() :
            "";
    return schemaAnnotation.description() + exampleDescription;
  }

  private static String buildDescriptionForEnum(ParameterWrapper field) {
    return buildDescriptionForEnumType((Class<Enum<?>>) field.getType());
  }

  private static String buildDescriptionForEnumType(Class<Enum<?>> enumType) {
    String enumValueDescription = Arrays.stream(enumType.getEnumConstants()).map(Enum::name).map("\"%s\""::formatted).collect(
        Collectors.joining(","));
    return "Must be one of [" + enumValueDescription + "]";
  }

  private static String buildDescriptionForPattern(ParameterWrapper field) {
    Pattern patternAnnotation = field.getAnnotation(Pattern.class);
    String regexp = patternAnnotation.regexp();
    return "Must conform to regex " + regexp;
  }

  private static String buildDescriptionForSize(ParameterWrapper field) {
    Size sizeAnnotation = field.getAnnotation(Size.class);
    return "Length must be between " + sizeAnnotation.min() + " and " + sizeAnnotation.max();
  }

}
