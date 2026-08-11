package ch.sbb.atlas.user.administration.module.useradministration.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.kafka.model.user.admin.PermissionRestrictionType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.module.useradministration.entity.PermissionRestriction;
import ch.sbb.atlas.user.administration.module.useradministration.entity.UserPermission;
import ch.sbb.atlas.user.administration.module.useradministration.service.UserPermissionRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class CustomUserPermissionRepositoryImplTest {

  private static final String SBOID = "ch:1:sboid:10009";

  private final UserPermissionRepository userPermissionRepository;

  @Autowired
  CustomUserPermissionRepositoryImplTest(UserPermissionRepository userPermissionRepository) {
    this.userPermissionRepository = userPermissionRepository;
  }

  @BeforeEach
  void setUp() {
    // 25 writers restricted to SBOID matching the filter, plus one reader that must be excluded,
    // plus one unrelated user without the restriction that must not match.
    for (int i = 0; i < 25; i++) {
      saveUserWithRestriction("u1000" + i, ApplicationRole.WRITER);
    }
    saveUserWithRestriction("u-reader", ApplicationRole.READER);

    userPermissionRepository.saveAndFlush(UserPermission.builder()
        .sbbUserId("u-unrelated")
        .role(ApplicationRole.SUPERVISOR)
        .application(ApplicationType.SEPODI)
        .build());
  }

  private void saveUserWithRestriction(String sbbUserId, ApplicationRole role) {
    UserPermission userPermission = UserPermission.builder()
        .sbbUserId(sbbUserId)
        .role(role)
        .application(ApplicationType.SEPODI)
        .build();
    PermissionRestriction restriction = PermissionRestriction.builder()
        .userPermission(userPermission)
        .type(PermissionRestrictionType.BUSINESS_ORGANISATION)
        .restriction(SBOID)
        .build();
    userPermission.getPermissionRestrictions().add(restriction);
    userPermissionRepository.saveAndFlush(userPermission);
  }

  @Test
  void shouldReturnAllMatchingUserIdsUnpagedBeyondOnePageSize() {
    // when
    List<String> userIds = userPermissionRepository.getAllFilteredUserIds(Set.of(ApplicationType.SEPODI), Set.of(SBOID),
        PermissionRestrictionType.BUSINESS_ORGANISATION);

    // then: all 25 writers are returned in one unpaged call (more than the default page size),
    // the reader is excluded, and the unrelated user without the restriction is excluded.
    assertThat(userIds)
        .hasSize(25)
        .doesNotContain("u-reader", "u-unrelated");
  }

  @Test
  void shouldApplySameHavingAndReaderExclusionRulesAsPagedQuery() {
    // when
    List<String> unpagedIds = userPermissionRepository.getAllFilteredUserIds(Set.of(ApplicationType.SEPODI), Set.of(SBOID),
        PermissionRestrictionType.BUSINESS_ORGANISATION);
    List<String> pagedIds = userPermissionRepository.getFilteredUsers(Pageable.ofSize(100), Set.of(ApplicationType.SEPODI),
        Set.of(SBOID), PermissionRestrictionType.BUSINESS_ORGANISATION).getContent();

    // then: unpaged and paged queries agree on which users match (same predicates/HAVING clause)
    assertThat(unpagedIds).containsExactlyInAnyOrderElementsOf(pagedIds);
  }

  @Test
  void shouldReturnEmptyListWhenNoUserMatchesFilter() {
    // when
    List<String> userIds = userPermissionRepository.getAllFilteredUserIds(Set.of(ApplicationType.SEPODI),
        Set.of("ch:1:sboid:99999"), PermissionRestrictionType.BUSINESS_ORGANISATION);

    // then
    assertThat(userIds).isEmpty();
  }
}
