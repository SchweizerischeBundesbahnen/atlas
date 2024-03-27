package ch.sbb.atlas.restdoc;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class FieldDescriptorsTest {

  @Test
  void shouldDescribeLineVersionModel() {
    FieldDescriptors fieldDescriptors = new FieldDescriptors(LineVersionModel.class);
    assertThat(fieldDescriptors.getFields()).hasSize(29);
  }

  @Test
  void shouldDescribeCreateServicePointVersionModel() {
    FieldDescriptors fieldDescriptors = new FieldDescriptors(CreateServicePointVersionModel.class);
    assertThat(fieldDescriptors.getFields()).hasSize(30);

    Map<String, FieldDescriptor> fieldDescriptorMap = fieldDescriptors.getFields().stream()
        .collect(Collectors.toMap(FieldDescriptor::getName, Function.identity()));

    FieldDescriptor nativeBooleanField = fieldDescriptorMap.get("freightServicePoint");
    assertThat(nativeBooleanField.getType()).isEqualTo("Boolean");
    assertThat(nativeBooleanField.isOptional()).isFalse();

    FieldDescriptor nestedObjectField = fieldDescriptorMap.get("servicePointGeolocation");
    assertThat(nestedObjectField.getType()).isEqualTo("Object");
    assertThat(nestedObjectField.isOptional()).isTrue();

    FieldDescriptor collectionField = fieldDescriptorMap.get("categories");
    assertThat(collectionField.getType()).isEqualTo("Array[String]");
    assertThat(collectionField.isOptional()).isTrue();
  }
}