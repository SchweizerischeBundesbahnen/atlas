package ch.sbb.atlas.kafka.model.user.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PermissionRestrictionTypeTest {

  @Test
  void shouldProvideRestrictionTypes() {
    assertThat(PermissionRestrictionType.values()).isNotEmpty();
  }
}