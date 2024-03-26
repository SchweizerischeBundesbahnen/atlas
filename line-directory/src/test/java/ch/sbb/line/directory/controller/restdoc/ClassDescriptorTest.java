package ch.sbb.line.directory.controller.restdoc;

import ch.sbb.atlas.api.lidi.LineVersionModel;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import org.junit.jupiter.api.Test;

class ClassDescriptorTest {

  @Test
  void shouldDescibeType() {
    ClassDescriptor classDescriptor = new ClassDescriptor(LineVersionModel.class);
    System.out.println(classDescriptor);

  }

  @Test
  void shouldDescibeservicepointType() {
    // TODO: super/nested classes
    ClassDescriptor classDescriptor = new ClassDescriptor(CreateServicePointVersionModel.class);
    System.out.println(classDescriptor);

  }
}