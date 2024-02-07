package ch.sbb.prm.directory.security;

import static ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType.PLATFORM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionModel;
import ch.sbb.atlas.kafka.model.user.admin.UserAdministrationPermissionRestrictionModel;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.security.UserPermissionHolder;
import ch.sbb.prm.directory.ContactPointTestData;
import ch.sbb.prm.directory.ParkingLotTestData;
import ch.sbb.prm.directory.PlatformTestData;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.RelationTestData;
import ch.sbb.prm.directory.SharedServicePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.ToiletTestData;
import ch.sbb.prm.directory.entity.PrmSharedVersion;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

@IntegrationTest
class PrmUserAdministrationServiceTest {

  @MockBean
  private UserPermissionHolder userPermissionHolder;

  private final SharedServicePointRepository sharedServicePointRepository;

  private final PrmUserAdministrationService prmBOBasedUserAdministrationService;

    private static final SharedServicePoint sharedServicePoint1 =
        SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:12345", Set.of("ch:1:sboid:100001", "ch:1:sboid:100002",
            "ch:1:sboid:100003", "ch:1:sboid:100004", "ch:1:sboid:100005"), Set.of("ch:1:sloid:12345:1"));

    private static final SharedServicePoint sharedServicePoint2 =
        SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:12345", Set.of("ch:1:sboid:100001", "ch:1:sboid:100002",
            "ch:1:sboid:100003", "ch:1:sboid:100004", "ch:1:sboid:100005"), Set.of("ch:1:sloid:12345:1"));

    private static final SharedServicePoint sharedServicePoint3 =
        SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:8507000", Set.of("ch:1:sboid:100001", "ch:1:sboid:100002",
            "ch:1:sboid:100003", "ch:1:sboid:100004", "ch:1:sboid:100005"), Set.of("ch:1:sloid:8507000:1"));


  @Autowired
  public PrmUserAdministrationServiceTest(SharedServicePointRepository sharedServicePointRepository,
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
  }

  @AfterEach
  void cleanUp() {
    sharedServicePointRepository.deleteAll();
  }

  private static Stream<Arguments> provideParameters() {
    String relationSloid = "ch:1:sloid:8507000:1";
    String parentServicePointSloid = "ch:1:sloid:8507000";
    return Stream.of(
        Arguments.of(StopPointTestData.getStopPointVersion(), sharedServicePoint2),
        Arguments.of(ContactPointTestData.getContactPointVersion(), sharedServicePoint1),
        Arguments.of(ParkingLotTestData.getParkingLotVersion(), sharedServicePoint1),
        Arguments.of(PlatformTestData.getPlatformVersion(), sharedServicePoint2),
        Arguments.of(ReferencePointTestData.getReferencePointVersion(), sharedServicePoint1),
        Arguments.of(RelationTestData.getRelation(parentServicePointSloid, relationSloid, PLATFORM), sharedServicePoint3),
        Arguments.of(ContactPointTestData.getContactPointVersion(), sharedServicePoint1),
        Arguments.of(ToiletTestData.getToiletVersion(), sharedServicePoint1)
    );
  }

  @ParameterizedTest
  @MethodSource("provideParameters")
  void shouldAllowCreateToAdminUser(PrmSharedVersion prmSharedVersion, SharedServicePoint sharedServicePoint) {
    sharedServicePointRepository.saveAndFlush(sharedServicePoint);
    when(userPermissionHolder.isAdmin()).thenReturn(true);

    boolean permissionsToCreate = prmBOBasedUserAdministrationService
        .hasUserRightsToCreateOrEditPrmObject(prmSharedVersion);

    assertThat(permissionsToCreate).isTrue();
  }

  @ParameterizedTest
  @MethodSource("provideParameters")
  void shouldAllowCreateToSupervisorUser(PrmSharedVersion prmSharedVersion, SharedServicePoint sharedServicePoint) {
    sharedServicePointRepository.saveAndFlush(sharedServicePoint);
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.PRM)
                .role(ApplicationRole.SUPERVISOR)
                .build()))
        .build()));

    boolean permissionsToCreate = prmBOBasedUserAdministrationService
        .hasUserRightsToCreateOrEditPrmObject(prmSharedVersion);

    assertThat(permissionsToCreate).isTrue();
  }

  @ParameterizedTest
  @MethodSource("provideParameters")
  void shouldAllowCreateToSuperUser(PrmSharedVersion prmSharedVersion, SharedServicePoint sharedServicePoint) {
    sharedServicePointRepository.saveAndFlush(sharedServicePoint);
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.PRM)
                .role(ApplicationRole.SUPER_USER)
                .build()))
        .build()));

    boolean permissionsToCreate = prmBOBasedUserAdministrationService
        .hasUserRightsToCreateOrEditPrmObject(prmSharedVersion);

    assertThat(permissionsToCreate).isTrue();
  }

  @ParameterizedTest
  @MethodSource("provideParameters")
  void shouldNotAllowCreateToReaderUser(PrmSharedVersion prmSharedVersion, SharedServicePoint sharedServicePoint) {
    sharedServicePointRepository.saveAndFlush(sharedServicePoint);
    when(userPermissionHolder.getCurrentUser()).thenReturn(Optional.of(UserAdministrationModel.builder()
        .userId("e123456")
        .permissions(Set.of(
            UserAdministrationPermissionModel.builder()
                .application(ApplicationType.PRM)
                .role(ApplicationRole.READER)
                .build()))
        .build()));

    boolean permissionsToCreate = prmBOBasedUserAdministrationService
        .hasUserRightsToCreateOrEditPrmObject(prmSharedVersion);

    assertThat(permissionsToCreate).isFalse();
  }

  @ParameterizedTest
  @MethodSource("provideParameters")
  void shouldAllowCreateToWriterUserWithAppropriateBO(PrmSharedVersion prmSharedVersion, SharedServicePoint sharedServicePoint) {
    sharedServicePointRepository.saveAndFlush(sharedServicePoint);
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
        .hasUserRightsToCreateOrEditPrmObject(prmSharedVersion);

    assertThat(permissionsToCreate).isTrue();
  }

  @ParameterizedTest
  @MethodSource("provideParameters")
  void shouldNotAllowCreateToWriterUserWithInappropriateBO(PrmSharedVersion prmSharedVersion,
      SharedServicePoint sharedServicePoint) {
    sharedServicePointRepository.saveAndFlush(sharedServicePoint);
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
        .hasUserRightsToCreateOrEditPrmObject(prmSharedVersion);

    assertThat(permissionsToCreate).isFalse();
  }

  @ParameterizedTest
  @MethodSource("provideParameters")
  void shouldAllowCreateToWriterUserWithAppropriateBOs(PrmSharedVersion prmSharedVersion, SharedServicePoint sharedServicePoint) {
    sharedServicePointRepository.saveAndFlush(sharedServicePoint);
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
        .hasUserRightsToCreateOrEditPrmObject(prmSharedVersion);

    assertThat(permissionsToCreate).isTrue();
  }

}
