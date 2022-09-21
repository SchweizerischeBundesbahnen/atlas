package ch.sbb.atlas.user.administration.api;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.user.administration.enumeration.UserAccountStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserModelTest {

    @Test
    void shouldMapToKafkaModelCorrectly() {
        // Given
        UserModel userModel = UserModel.builder().sbbUserId("e123456").lastName("Gandalf").firstName("The Gray")
                .mail("gandalf@sbb.ch").displayName("White Gandalf").accountStatus(UserAccountStatus.ACTIVE)
                .permissions(Set.of(UserPermissionModel.builder().role(ApplicationRole.SUPERVISOR)
                        .application(ApplicationType.TTFN).build())).build();
        // When
        UserAdministrationModel userAdministrationModel = userModel.toKafkaModel();
        // Then
        UserAdministrationModel expected = UserAdministrationModel.builder().sbbUserId("e123456")
                .permissions(Set.of(UserAdministrationPermissionModel.builder().application(ApplicationType.TTFN)
                        .role(ApplicationRole.SUPERVISOR).build())).build();
        assertThat(userAdministrationModel).isEqualTo(expected);
    }

    @Test
    void shouldMapToKafkaModelWithSboidsCorrectly() {
        // Given
        UserModel userModel = UserModel.builder().sbbUserId("e123456").lastName("Gandalf").firstName("The Gray")
                .mail("gandalf@sbb.ch").displayName("White Gandalf").accountStatus(UserAccountStatus.ACTIVE)
                .permissions(Set.of(UserPermissionModel.builder().role(ApplicationRole.WRITER)
                        .application(ApplicationType.TTFN).sboids(List.of("beste sboid")).build())).build();
        // When
        UserAdministrationModel userAdministrationModel = userModel.toKafkaModel();
        // Then
        UserAdministrationModel expected = UserAdministrationModel.builder().sbbUserId("e123456")
                .permissions(Set.of(UserAdministrationPermissionModel.builder().application(ApplicationType.TTFN)
                        .role(ApplicationRole.WRITER).sboids(Set.of("beste sboid")).build())).build();
        assertThat(userAdministrationModel).isEqualTo(expected);
    }
}