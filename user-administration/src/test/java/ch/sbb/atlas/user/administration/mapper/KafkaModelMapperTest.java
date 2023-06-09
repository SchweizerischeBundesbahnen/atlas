package ch.sbb.atlas.user.administration.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.user.administration.SboidPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.UserModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.api.user.administration.enumeration.UserAccountStatus;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class KafkaModelMapperTest {

  @Test
  void shouldMapToKafkaModelCorrectly() {
    // Given
    UserModel userModel = UserModel.builder().sbbUserId("e123456").lastName("Gandalf").firstName("The Gray")
        .mail("gandalf@sbb.ch").displayName("White Gandalf").accountStatus(UserAccountStatus.ACTIVE)
        .permissions(Set.of(PermissionModel.builder().role(ApplicationRole.SUPERVISOR)
            .application(ApplicationType.TTFN).build())).build();
    // When
    UserAdministrationModel userAdministrationModel = KafkaModelMapper.toKafkaModel(userModel);
    // Then
    UserAdministrationModel expected = UserAdministrationModel.builder().userId("e123456")
        .permissions(Set.of(UserAdministrationPermissionModel.builder().application(ApplicationType.TTFN)
            .role(ApplicationRole.SUPERVISOR).build())).build();
    assertThat(userAdministrationModel).isEqualTo(expected);
  }

  @Test
  void shouldMapToKafkaModelWithSboidsCorrectly() {
    // Given
    UserModel userModel = UserModel.builder().sbbUserId("e123456").lastName("Gandalf").firstName("The Gray")
        .mail("gandalf@sbb.ch").displayName("White Gandalf").accountStatus(UserAccountStatus.ACTIVE)
        .permissions(Set.of(PermissionModel.builder().role(ApplicationRole.WRITER)
            .application(ApplicationType.TTFN).permissionRestrictions(List.of(new SboidPermissionRestrictionModel("beste sboid"))).build())).build();
    // When
    UserAdministrationModel userAdministrationModel = KafkaModelMapper.toKafkaModel(userModel);
    // Then
    UserAdministrationModel expected = UserAdministrationModel.builder().userId("e123456")
        .permissions(Set.of(UserAdministrationPermissionModel.builder().application(ApplicationType.TTFN)
            .role(ApplicationRole.WRITER).restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder().value(
                "beste sboid").restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION).build()
              )).build())).build();
    assertThat(userAdministrationModel).isEqualTo(expected);
  }
}