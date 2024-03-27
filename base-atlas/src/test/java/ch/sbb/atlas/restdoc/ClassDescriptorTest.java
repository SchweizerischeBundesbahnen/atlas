package ch.sbb.atlas.restdoc;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import org.junit.jupiter.api.Test;

class ClassDescriptorTest {

  @Test
  void shouldDescribeLineVersionModel() {
    FieldDescriptors fieldDescriptors = new FieldDescriptors(LineVersionModel.class);
    assertThat(fieldDescriptors.getFields()).hasSize(29);
  }

  @Test
  void shouldDescribeCreateServicePointVersionModel() {
    FieldDescriptors fieldDescriptors = new FieldDescriptors(CreateServicePointVersionModel.class);
    assertThat(fieldDescriptors.getFields()).hasSize(30);
  }
}