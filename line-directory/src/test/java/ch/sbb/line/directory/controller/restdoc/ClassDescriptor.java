package ch.sbb.line.directory.controller.restdoc;

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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Data
public class ClassDescriptor {

  private static final String LINE_SEPERATOR = "\n";

  private final List<FieldDescriptor> fields = new ArrayList<>();

  public ClassDescriptor(Class<?> clazz) {
    this.fields.addAll(
        Stream.of(clazz.getDeclaredFields()).filter(field -> !Modifier.isStatic(field.getModifiers())).map(FieldDescriptor::new)
            .toList());
  }

  @Data
  private static class FieldDescriptor {

    private final String fieldName;
    private final String type;
    private final boolean optional;
    private final String description;

    public FieldDescriptor(Field field) {
      this.fieldName = field.getName();
      this.type = field.getGenericType().getTypeName();
      this.optional = !isMandatory(field);
      this.description = String.join(LINE_SEPERATOR, buildDescriptionForField(field));
    }

    private boolean isMandatory(Field field) {
      if (field.getType().isPrimitive()) {
        return true;
      }
      return Arrays.stream(field.getAnnotations()).anyMatch(i ->
          i.annotationType().equals(NotNull.class) ||
              i.annotationType().equals(NotBlank.class) ||
              i.annotationType().equals(NotEmpty.class));
    }

    private List<String> buildDescriptionForField(Field field) {
      List<String> descriptionParts = new ArrayList<>();
      if (field.isAnnotationPresent(Schema.class)) {
        descriptionParts.add(buildDescriptionForSchema(field));
      }
      if (field.getType().isEnum()) {
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

    private String buildDescriptionForSchema(Field field) {
      Schema schemaAnnotation = field.getAnnotation(Schema.class);
      String exampleDescription =
          StringUtils.isNotBlank(schemaAnnotation.example()) ?
              LINE_SEPERATOR + "Example value: " + schemaAnnotation.example() :
              "";
      return schemaAnnotation.description() + exampleDescription;
    }

    private String buildDescriptionForEnum(Field field) {
      Class<Enum<?>> enumType = (Class<Enum<?>>) field.getType();
      String enumValueDescription = Arrays.stream(enumType.getEnumConstants()).map(Enum::name).map("\"%s\""::formatted).collect(
          Collectors.joining(","));
      return "Must be one of [" + enumValueDescription + "]";
    }

    private String buildDescriptionForPattern(Field field) {
      Pattern patternAnnotation = field.getAnnotation(Pattern.class);
      String regexp = patternAnnotation.regexp();
      return "Must conform to regex " + regexp;
    }

    private String buildDescriptionForSize(Field field) {
      Size sizeAnnotation = field.getAnnotation(Size.class);
      return "Length must be between " + sizeAnnotation.min() + " and " + sizeAnnotation.max();
    }
  }

}
