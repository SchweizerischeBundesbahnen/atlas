package ch.sbb.atlas.user.administration.module.useradministration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserPermissionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class UserPermissionRepositoryTest {

  private final UserPermissionRepository userPermissionRepository;

  @Autowired
  UserPermissionRepositoryTest(UserPermissionRepository userPermissionRepository) {
    this.userPermissionRepository = userPermissionRepository;
  }

  @BeforeEach
  void setUp() {
    UserPermission userPermissionInSepodi = UserPermission.builder()
        .sbbUserId("u123456")
        .role(ApplicationRole.SUPERVISOR)
        .application(ApplicationType.SEPODI)
        .build();

    UserPermission userPermissionInPRM = UserPermission.builder()
        .sbbUserId("***REMOVED***")
        .role(ApplicationRole.SUPERVISOR)
        .application(ApplicationType.PRM)
        .build();

    userPermissionRepository.saveAndFlush(userPermissionInSepodi);
    userPermissionRepository.saveAndFlush(userPermissionInPRM);
  }

  @Test
  void findBySbbUserIdIgnoreCase() {
    List<UserPermission> userPermission = userPermissionRepository.findBySbbUserIdIgnoreCase("***REMOVED***");
    List<UserPermission> userPermission2 = userPermissionRepository.findBySbbUserIdIgnoreCase("***REMOVED***");
    assertThat(userPermission).hasSize(1);
    assertThat(userPermission2).hasSize(1);
  }

  @Test
  void findBySbbUserIdIgnoreCaseAndApplication() {
    Optional<UserPermission> userPermission = userPermissionRepository.findBySbbUserIdIgnoreCaseAndApplication("u123456",
        ApplicationType.SEPODI);
    Optional<UserPermission> userPermission2 = userPermissionRepository.findBySbbUserIdIgnoreCaseAndApplication("U123456",
        ApplicationType.SEPODI);
    assertThat(userPermission.isPresent()).isTrue();
    assertThat(userPermission2.isPresent()).isTrue();
  }
}