package ch.sbb.prm.directory.security;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.service.SharedServicePointService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@IntegrationTest
public class PrmBusinessOrganisationBasedUserAdministrationServiceTest {

    @MockBean
    private UserPermissionHolder userPermissionHolder;

    private final SharedServicePointRepository sharedServicePointRepository;
    private final SharedServicePointService sharedServicePointService;

    private final PrmBusinessOrganisationBasedUserAdministrationService prmBOBasedUserAdministrationService;

    @Autowired
    public PrmBusinessOrganisationBasedUserAdministrationServiceTest(SharedServicePointRepository sharedServicePointRepository,
                                                                     SharedServicePointService sharedServicePointService,
                                                                     PrmBusinessOrganisationBasedUserAdministrationService prmBOBasedUserAdministrationService,
                                                                     UserPermissionHolder userPermissionHolder) {
        this.sharedServicePointRepository = sharedServicePointRepository;
        this.sharedServicePointService = sharedServicePointService;
        this.prmBOBasedUserAdministrationService = prmBOBasedUserAdministrationService;
        this.userPermissionHolder = userPermissionHolder;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userPermissionHolder.isAdmin()).thenReturn(false);
        when(userPermissionHolder.getCurrentUserSbbUid()).thenReturn("e123456");

        SharedServicePoint servicePoint = SharedServicePoint.builder()
                .servicePoint("{\"servicePointSloid\":\"ch:1.sloid:12345\",\"sboids\":[\"ch:1:sboid:100001\",\"ch:1:sboid:100002\",\"ch:1:sboid:100003\",\"ch:1:sboid:100004\",\"ch:1:sboid:100005\"],"
                        + "\"trafficPointSloids\":[\"ch:1.sloid:12345:1\"]}")
                .sloid("ch:1.sloid:12345")
                .build();
        sharedServicePointRepository.saveAndFlush(servicePoint);
    }

    @AfterEach
    void cleanUp() {
        sharedServicePointRepository.deleteAll();
    }

    @Test
    void shouldAllowCreateToAdminUser() {
        when(userPermissionHolder.isAdmin()).thenReturn(true);

        boolean permissionsToCreate = prmBOBasedUserAdministrationService
                .hasUserPermissionsForBusinessOrganisations(ToiletTestData.getToiletVersion(), ApplicationType.PRM);

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

        boolean permissionsToCreate = prmBOBasedUserAdministrationService
                .hasUserPermissionsForBusinessOrganisations(ToiletTestData.getToiletVersion(), ApplicationType.PRM);

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

        boolean permissionsToCreate = prmBOBasedUserAdministrationService
                .hasUserPermissionsForBusinessOrganisations(ToiletTestData.getToiletVersion(), ApplicationType.PRM);

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

        boolean permissionsToCreate = prmBOBasedUserAdministrationService
                .hasUserPermissionsForBusinessOrganisations(ToiletTestData.getToiletVersion(), ApplicationType.PRM);

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

        boolean permissionsToCreate = prmBOBasedUserAdministrationService
                .hasUserPermissionsForBusinessOrganisations(ToiletTestData.getToiletVersion(), ApplicationType.PRM);

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

        boolean permissionsToCreate = prmBOBasedUserAdministrationService
                .hasUserPermissionsForBusinessOrganisations(ToiletTestData.getToiletVersion(), ApplicationType.PRM);

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

        boolean permissionsToCreate = prmBOBasedUserAdministrationService
                .hasUserPermissionsForBusinessOrganisations(ToiletTestData.getToiletVersion(), ApplicationType.PRM);

        assertThat(permissionsToCreate).isTrue();
    }

}
