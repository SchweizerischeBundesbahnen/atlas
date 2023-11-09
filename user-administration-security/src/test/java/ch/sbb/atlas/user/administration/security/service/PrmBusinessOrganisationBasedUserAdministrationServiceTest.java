package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PrmBusinessOrganisationBasedUserAdministrationServiceTest {

    @Mock
    private UserPermissionHolder userPermissionHolder;

    private PrmBusinessOrganisationBasedUserAdministrationService prmBOBasedUserAdministrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        prmBOBasedUserAdministrationService =
                new PrmBusinessOrganisationBasedUserAdministrationService(userPermissionHolder);
        when(userPermissionHolder.isAdmin()).thenReturn(false);
        when(userPermissionHolder.getCurrentUserSbbUid()).thenReturn("e123456");
    }

    private SharedServicePointVersionModel getSomeSharedServicePointVersionModels() {
        return SharedServicePointVersionModel.builder()
                .servicePointSloid("ch:1:sloid:90499")
                .sboids(Set.of("ch:1:sboid:100001","ch:1:sboid:100002","ch:1:sboid:100003","ch:1:sboid:100004","ch:1:sboid:100005"))
                .trafficPointSloids(Set.of("ch:1:sloid:12345:1"))
                .build();
    }

    @Test
    void shouldAllowCreateToAdminUser() {
        when(userPermissionHolder.isAdmin()).thenReturn(true);

        boolean permissionsToCreate = prmBOBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations(
                getSomeSharedServicePointVersionModels(), ApplicationType.PRM);

        assertThat(permissionsToCreate).isTrue();
    }

    @Test
    void shouldAllowCreateToSupervisorUser() {
        when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
                .userId("e123456")
                .permissions(Set.of(
                        UserAdministrationPermissionModel.builder()
                                .application(ApplicationType.PRM)
                                .role(ApplicationRole.SUPERVISOR)
                                .build()))
                .build()));

        boolean permissionsToCreate = prmBOBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations(
                getSomeSharedServicePointVersionModels(), ApplicationType.PRM);

        assertThat(permissionsToCreate).isTrue();
    }

    @Test
    void shouldAllowCreateToSuperUser() {
        when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
                .userId("e123456")
                .permissions(Set.of(
                        UserAdministrationPermissionModel.builder()
                                .application(ApplicationType.PRM)
                                .role(ApplicationRole.SUPER_USER)
                                .build()))
                .build()));

        boolean permissionsToCreate = prmBOBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations(
                getSomeSharedServicePointVersionModels(), ApplicationType.PRM);

        assertThat(permissionsToCreate).isTrue();
    }

    @Test
    void shouldNotAllowCreateToReaderUser() {
        when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
                .userId("e123456")
                .permissions(Set.of(
                        UserAdministrationPermissionModel.builder()
                                .application(ApplicationType.PRM)
                                .role(ApplicationRole.READER)
                                .build()))
                .build()));

        boolean permissionsToCreate = prmBOBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations(
                getSomeSharedServicePointVersionModels(), ApplicationType.PRM);

        assertThat(permissionsToCreate).isFalse();
    }

    @Test
    void shouldAllowToCreateToWriterUserWithAppropriateBO() {
        when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
                .userId("e123456")
                .permissions(Set.of(
                        UserAdministrationPermissionModel.builder()
                                .application(ApplicationType.PRM)
                                .role(ApplicationRole.WRITER)
                                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                                                .value("ch:1:sboid:100001")
                                                .restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION)
                                                .build()))
                                .build()))
                .build()));

        boolean permissionsToCreate = prmBOBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations(
                getSomeSharedServicePointVersionModels(), ApplicationType.PRM);

        assertThat(permissionsToCreate).isTrue();
    }

    @Test
    void shouldNotAllowToCreateToWriterUserWithInappropriateBO() {
        when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
                .userId("e123456")
                .permissions(Set.of(
                        UserAdministrationPermissionModel.builder()
                                .application(ApplicationType.PRM)
                                .role(ApplicationRole.WRITER)
                                .restrictions(Set.of(UserAdministrationPermissionRestrictionModel.builder()
                                        .value("ch:1:sboid:100011")
                                        .restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION)
                                        .build()))
                                .build()))
                .build()));

        boolean permissionsToCreate = prmBOBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations(
                getSomeSharedServicePointVersionModels(), ApplicationType.PRM);

        assertThat(permissionsToCreate).isFalse();
    }

    @Test
    void shouldAllowToCreateToWriterUserWithAppropriateBOs() {
        when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
                .userId("e123456")
                .permissions(Set.of(
                        UserAdministrationPermissionModel.builder()
                                .application(ApplicationType.PRM)
                                .role(ApplicationRole.WRITER)
                                .restrictions(Set.of(
                                        UserAdministrationPermissionRestrictionModel.builder()
                                        .value("ch:1:sboid:100005")
                                        .restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION)
                                        .build(),
                                        UserAdministrationPermissionRestrictionModel.builder()
                                        .value("ch:1:sboid:100001")
                                        .restrictionType(PermissionRestrictionType.BUSINESS_ORGANISATION)
                                        .build()))
                                .build()))
                .build()));

        boolean permissionsToCreate = prmBOBasedUserAdministrationService.hasUserPermissionsForBusinessOrganisations(
                getSomeSharedServicePointVersionModels(), ApplicationType.PRM);

        assertThat(permissionsToCreate).isTrue();
    }

}
