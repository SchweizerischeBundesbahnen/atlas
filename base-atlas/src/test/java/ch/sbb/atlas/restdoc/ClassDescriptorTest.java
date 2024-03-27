package ch.sbb.atlas.restdoc;

import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import org.junit.jupiter.api.Test;

class ClassDescriptorTest {

  @Test
  void shouldDescibeType() {
    FieldDescriptors fieldDescriptors = new FieldDescriptors(LineVersionModel.class);
    System.out.println(fieldDescriptors);

  }

  @Test
  void shouldDescibeservicepointType() {
    // TODO: super/nested classes
    FieldDescriptors fieldDescriptors = new FieldDescriptors(CreateServicePointVersionModel.class);
    System.out.println(fieldDescriptors);

  }
}