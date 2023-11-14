package ch.sbb.prm.directory.security;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import ch.sbb.prm.directory.entity.PrmSharedVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@IntegrationTest
public class BasePrmUserAdministrationServiceTest {

    @MockBean
    private UserPermissionHolder userPermissionHolder;

    private final SharedServicePointRepository sharedServicePointRepository;

    private final PrmUserAdministrationService prmBOBasedUserAdministrationService;

    @Autowired
    public BasePrmUserAdministrationServiceTest(SharedServicePointRepository sharedServicePointRepository,
                                                PrmUserAdministrationService prmBOBasedUserAdministrationService,
                                                UserPermissionHolder userPermissionHolder) {
        this.sharedServicePointRepository = sharedServicePointRepository;
        this.prmBOBasedUserAdministrationService = prmBOBasedUserAdministrationService;
        this.userPermissionHolder = userPermissionHolder;
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(userPermissionHolder.isAdmin()).thenReturn(false);
        when(userPermissionHolder.getCurrentUserSbbUid()).thenReturn("e123456");

        SharedServicePoint servicePoint = getSharedServicePoint();
        sharedServicePointRepository.saveAndFlush(servicePoint);
    }

    protected SharedServicePoint getSharedServicePoint() {
        SharedServicePoint servicePoint = SharedServicePoint.builder()
                .servicePoint("{\"servicePointSloid\":\"ch:1.sloid:12345\",\"sboids\":[\"ch:1:sboid:100001\",\"ch:1:sboid:100002\",\"ch:1:sboid:100003\",\"ch:1:sboid:100004\",\"ch:1:sboid:100005\"],"
                        + "\"trafficPointSloids\":[\"ch:1.sloid:12345:1\"]}")
                .sloid("ch:1.sloid:12345")
                .build();
        return servicePoint;
    }

    @AfterEach
    void cleanUp() {
        sharedServicePointRepository.deleteAll();
    }

    protected void allowCreateToAdminUser(PrmSharedVersion prmSharedVersion) {
        when(userPermissionHolder.isAdmin()).thenReturn(true);

        boolean permissionsToCreate = prmBOBasedUserAdministrationService
                .hasUserPermissionsForBusinessOrganisations(prmSharedVersion, ApplicationType.PRM);

        assertThat(permissionsToCreate).isTrue();
    }

    protected void allowCreateToSupervisorUser(PrmSharedVersion prmSharedVersion) {
        when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
                .userId("e123456")
                .permissions(Set.of(
                        UserAdministrationPermissionModel.builder()
                                .application(ApplicationType.PRM)
                                .role(ApplicationRole.SUPERVISOR)
                                .build()))
                .build()));

        boolean permissionsToCreate = prmBOBasedUserAdministrationService
                .hasUserPermissionsForBusinessOrganisations(prmSharedVersion, ApplicationType.PRM);

        assertThat(permissionsToCreate).isTrue();
    }

    protected void allowCreateToSuperUser(PrmSharedVersion prmSharedVersion) {
        when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
                .userId("e123456")
                .permissions(Set.of(
                        UserAdministrationPermissionModel.builder()
                                .application(ApplicationType.PRM)
                                .role(ApplicationRole.SUPER_USER)
                                .build()))
                .build()));

        boolean permissionsToCreate = prmBOBasedUserAdministrationService
                .hasUserPermissionsForBusinessOrganisations(prmSharedVersion, ApplicationType.PRM);

        assertThat(permissionsToCreate).isTrue();
    }

    protected void notAllowCreateToReaderUser(PrmSharedVersion prmSharedVersion) {
        when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
                .userId("e123456")
                .permissions(Set.of(
                        UserAdministrationPermissionModel.builder()
                                .application(ApplicationType.PRM)
                                .role(ApplicationRole.READER)
                                .build()))
                .build()));

        boolean permissionsToCreate = prmBOBasedUserAdministrationService
                .hasUserPermissionsForBusinessOrganisations(prmSharedVersion, ApplicationType.PRM);

        assertThat(permissionsToCreate).isFalse();
    }

    protected void allowCreateToWriterUserWithAppropriateBO(PrmSharedVersion prmSharedVersion) {
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
                .hasUserPermissionsForBusinessOrganisations(prmSharedVersion, ApplicationType.PRM);

        assertThat(permissionsToCreate).isTrue();
    }

    protected void notAllowCreateToWriterUserWithInappropriateBO(PrmSharedVersion prmSharedVersion) {
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
                .hasUserPermissionsForBusinessOrganisations(prmSharedVersion, ApplicationType.PRM);

        assertThat(permissionsToCreate).isFalse();
    }

    protected void allowCreateToWriterUserWithAppropriateBOs(PrmSharedVersion prmSharedVersion) {
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
                .hasUserPermissionsForBusinessOrganisations(prmSharedVersion, ApplicationType.PRM);

        assertThat(permissionsToCreate).isTrue();
    }

}
