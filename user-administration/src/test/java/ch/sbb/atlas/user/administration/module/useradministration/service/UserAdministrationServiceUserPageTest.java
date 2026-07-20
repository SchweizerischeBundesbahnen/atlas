package ch.sbb.atlas.user.administration.module.useradministration.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.module.useradministration.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@IntegrationTest
class UserAdministrationServiceUserPageTest {

  @Autowired private UserPermissionRepository userPermissionRepository;
  @Autowired private UserAdministrationService userAdministrationService;

  @BeforeEach
  void setUp() {
    List<UserPermission> userPermissions = new ArrayList<>();

    UserPermission userPermission = UserPermission.builder().sbbUserId("u123456").application(ApplicationType.TTFN)
        .role(ApplicationRole.WRITER).build();
    userPermission.setPermissionRestrictions(new HashSet<>(List.of(PermissionRestriction.builder()
            .userPermission(userPermission)
            .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
            .restriction("ch:1:sboid:100")
            .build(),
        PermissionRestriction.builder()
            .userPermission(userPermission)
            .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
            .restriction("ch:1:sboid:101")
            .build(),
        PermissionRestriction.builder()
            .userPermission(userPermission)
            .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
            .restriction("ch:1:sboid:102")
            .build())));
    userPermissions.add(userPermission);

    userPermissions.add(
        UserPermission.builder().sbbUserId("u123456").application(ApplicationType.LIDI).role(ApplicationRole.SUPERVISOR).build());

    userPermission =
        UserPermission.builder().sbbUserId("e654321").application(ApplicationType.TTFN).role(ApplicationRole.WRITER).build();
    userPermission.setPermissionRestrictions(new HashSet<>(List.of(PermissionRestriction.builder()
            .userPermission(userPermission)
            .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
            .restriction("ch:1:sboid:100")
            .build(),
        PermissionRestriction.builder()
            .userPermission(userPermission)
            .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
            .restriction("ch:1:sboid:101")
            .build())));
    userPermissions.add(userPermission);

    userPermission =
        UserPermission.builder().sbbUserId("e654321").application(ApplicationType.LIDI).role(ApplicationRole.WRITER).build();
    userPermission.setPermissionRestrictions(new HashSet<>(List.of(PermissionRestriction.builder()
            .userPermission(userPermission)
            .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
            .restriction("ch:1:sboid:100")
            .build(),
        PermissionRestriction.builder()
            .userPermission(userPermission)
            .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
            .restriction("ch:1:sboid:101")
            .build())));
    userPermissions.add(userPermission);

    userPermissions.add(
        UserPermission.builder().sbbUserId("u111111").application(ApplicationType.LIDI).role(ApplicationRole.READER).build());

    userPermissionRepository.saveAll(userPermissions);
  }

  @AfterEach
  void cleanup() {
    userPermissionRepository.deleteAll();
  }

  @Test
  void shouldReturnAllUsersWhenNoFiltersApplied() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(20), null, null, null);

    assertThat(userPage.getTotalElements()).isEqualTo(3);
    assertThat(userPage.getContent()).containsExactlyInAnyOrder("u123456", "e654321", "u111111");
  }

  @Test
  void shouldFilterByApplicationTypes() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(20), null,
        new HashSet<>(List.of(ApplicationType.TTFN, ApplicationType.LIDI)), null);

    assertThat(userPage.getTotalElements()).isEqualTo(2);
    assertThat(userPage.getContent()).containsExactlyInAnyOrder("u123456", "e654321");
  }

  @Test
  void shouldFilterBySboidRestrictions() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(20),
        new HashSet<>(List.of("ch:1:sboid:100", "ch:1:sboid:101")), null, PermissionRestrictionType.BUSINESS_ORGANISATION);

    assertThat(userPage.getTotalElements()).isEqualTo(2);
    assertThat(userPage.getContent()).containsExactlyInAnyOrder("u123456", "e654321");
  }

  @Test
  void shouldExcludeReadersWhenFilteringByApplicationType() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(20), null,
        new HashSet<>(List.of(ApplicationType.LIDI)), null);

    assertThat(userPage.getTotalElements()).isEqualTo(2);
    assertThat(userPage.getContent()).containsExactlyInAnyOrder("u123456", "e654321");
  }

  @Test
  void shouldFilterByApplicationTypesAndSboids() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(20),
        new HashSet<>(List.of("ch:1:sboid:100", "ch:1:sboid:101")),
        new HashSet<>(List.of(ApplicationType.TTFN, ApplicationType.LIDI)), PermissionRestrictionType.BUSINESS_ORGANISATION);

    assertThat(userPage.getTotalElements()).isEqualTo(1);
    assertThat(userPage.getContent()).containsExactlyInAnyOrder("e654321");
  }

  @Test
  void shouldReturnOnlyUserWithSpecificSboidAndApplicationType() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(20),
        new HashSet<>(List.of("ch:1:sboid:102")),
        new HashSet<>(List.of(ApplicationType.TTFN)), PermissionRestrictionType.BUSINESS_ORGANISATION);

    assertThat(userPage.getTotalElements()).isEqualTo(1);
    assertThat(userPage.getContent()).containsExactlyInAnyOrder("u123456");
  }

  @Test
  void shouldPageResults() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(1), new HashSet<>(List.of("ch:1:sboid:100")),
        new HashSet<>(List.of(ApplicationType.TTFN)), PermissionRestrictionType.BUSINESS_ORGANISATION);

    assertThat(userPage.getTotalElements()).isEqualTo(2);
    assertThat(userPage.getContent()).hasSize(1);
  }

  @Test
  void shouldGetAllUserIds() {
    List<String> userIds = userAdministrationService.getAllUserIds();
    assertThat(userIds).hasSize(3).containsExactlyInAnyOrder("u123456", "e654321", "u111111");
  }

}
