package ch.sbb.atlas.auto.rest.doc.descriptor;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

@UtilityClass
class FieldDescriptionBuilder {

  @UtilityClass
  static class TypeDescriptionBuilder {

    static String getType(ParameterWrapper field) {
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
        case "java.lang.Byte", "java.lang.Short", "java.lang.Long", "java.lang.Integer", "java.math.BigInteger", "int", "long" ->
            "Integer";
        case "java.lang.Float", "java.lang.Double", "java.math.BigDecimal", "double" -> "Decimal";
        case "java.lang.Character", "java.lang.String", "java.time.Instant", "java.util.Locale",
            "java.time.LocalDate", "java.time.LocalDateTime" -> "String";
        case "java.lang.Boolean", "boolean" -> "Boolean";
        case "org.springframework.web.multipart.MultipartFile" -> "File";
        default -> "Object";
      };
    }
  }

  @UtilityClass
  static class MandatoryBuilder {

    static boolean isMandatory(ParameterWrapper field) {
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
  }

  @UtilityClass
  static class DescriptionBuilder {

    static List<String> buildDescriptionForField(ParameterWrapper field) {
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
              FieldDescriptors.LINE_SEPARATOR + FieldDescriptors.LINE_SEPARATOR + "Example value: " + schemaAnnotation.example() :
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
}
