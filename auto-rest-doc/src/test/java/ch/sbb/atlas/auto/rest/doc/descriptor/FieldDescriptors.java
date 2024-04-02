package ch.sbb.atlas.auto.rest.doc.descriptor;

import ch.sbb.atlas.auto.rest.doc.descriptor.FieldDescriptionBuilder.DescriptionBuilder;
import ch.sbb.atlas.auto.rest.doc.descriptor.FieldDescriptionBuilder.MandatoryBuilder;
import ch.sbb.atlas.auto.rest.doc.descriptor.FieldDescriptionBuilder.TypeDescriptionBuilder;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import org.springframework.core.MethodParameter;

@Getter
@Data
public class FieldDescriptors {

  static final String LINE_SEPARATOR = "\n";

  private final List<FieldDescriptor> fields = new ArrayList<>();

  public FieldDescriptors(Class<?> clazz) {
    this.fields.addAll(
        getFieldsIncludingSuperclasses(clazz).stream()
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

  private static List<Field> getFieldsIncludingSuperclasses(Class<?> clazz) {
    List<Field> fields = new ArrayList<>(List.of(clazz.getDeclaredFields()));
    if (fields.isEmpty()) {
      return Collections.emptyList();
    } else {
      fields.addAll(getFieldsIncludingSuperclasses(clazz.getSuperclass()));
    }
    return fields;
  }

  private static List<FieldDescriptor> buildFieldDescriptions(ParameterWrapper field) {
    List<FieldDescriptor> fieldDescriptions = new ArrayList<>();
    FieldDescriptor currentField = FieldDescriptor.builder()
        .name(field.getName())
        .type(TypeDescriptionBuilder.getType(field))
        .optional(!MandatoryBuilder.isMandatory(field))
        .description(String.join(LINE_SEPARATOR + LINE_SEPARATOR, DescriptionBuilder.buildDescriptionForField(field)))
        .build();
    fieldDescriptions.add(currentField);

    if (currentField.getType().equals("Array[Object]")) {
      Class<?> collectionType = field.getType();
      FieldDescriptors collectionTypeDescription = new FieldDescriptors(collectionType);
      List<FieldDescriptor> collectionFields = collectionTypeDescription.getFields();
      collectionFields.forEach(
          collectionField -> collectionField.setName(currentField.getName() + "[]." + collectionField.getName()));
      fieldDescriptions.addAll(collectionFields);
    }
    if (currentField.getType().equals("Object")) {
      Class<?> subType = field.getType();
      FieldDescriptors subTypeDescription = new FieldDescriptors(subType);
      List<FieldDescriptor> collectionFields = subTypeDescription.getFields();
      collectionFields.forEach(
          collectionField -> collectionField.setName(currentField.getName() + "." + collectionField.getName()));
      fieldDescriptions.addAll(collectionFields);
    }
    return fieldDescriptions;
  }

}
