package ch.sbb.atlas.kafka.model.user.admin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class KafkaSerializationUserAdministrationModelTest {

  @Test
  void shouldNotChangePackageNameBecauseItIsStoredInKafka() {
    assertThat(UserAdministrationModel.class.getName()).isEqualTo("ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel");
    assertThat(UserAdministrationPermissionModel.class.getName()).isEqualTo("ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel");

    assertThat(ApplicationRole.class.getName()).isEqualTo("ch.sbb.atlas.kafka.model.user.admin.ApplicationRole");
    assertThat(ApplicationType.class.getName()).isEqualTo("ch.sbb.atlas.kafka.model.user.admin.ApplicationType");
  }
}