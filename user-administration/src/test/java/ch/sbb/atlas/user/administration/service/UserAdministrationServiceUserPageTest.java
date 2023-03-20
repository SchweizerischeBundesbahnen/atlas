package ch.sbb.atlas.user.administration.service;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.user.administration.entity.UserPermission;
import ch.sbb.atlas.user.administration.repository.UserPermissionRepository;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@IntegrationTest
public class UserAdministrationServiceUserPageTest {

  @Autowired private UserPermissionRepository userPermissionRepository;
  @Autowired private UserAdministrationService userAdministrationService;

  @BeforeEach
  void setUp() {
    List<UserPermission> userPermissions = List.of(
        UserPermission.builder().sbbUserId("u123456").application(ApplicationType.TTFN).role(ApplicationRole.WRITER)
            .sboid(new HashSet<>(List.of("ch:1:sboid:100", "ch:1:sboid:101", "ch:1:sboid:102"))).build(),

        UserPermission.builder().sbbUserId("u123456").application(ApplicationType.LIDI).role(ApplicationRole.SUPERVISOR).build(),

        UserPermission.builder().sbbUserId("e654321").application(ApplicationType.TTFN).role(ApplicationRole.WRITER)
            .sboid(new HashSet<>(List.of("ch:1:sboid:100", "ch:1:sboid:101"))).build(),

        UserPermission.builder().sbbUserId("e654321").application(ApplicationType.LIDI).role(ApplicationRole.WRITER)
            .sboid(new HashSet<>(List.of("ch:1:sboid:100", "ch:1:sboid:101"))).build(),

        UserPermission.builder().sbbUserId("u111111").application(ApplicationType.LIDI).role(ApplicationRole.READER).build());
    userPermissionRepository.saveAll(userPermissions);
  }

  @AfterEach
  void cleanup() {
    userPermissionRepository.deleteAll();
  }

  @Test
  void testWithoutAppTypesWithoutSboids() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(20), null, null, null);
    Assertions.assertEquals(2, userPage.getTotalElements());
    Assertions.assertEquals(2, userPage.getContent().size());
    Assertions.assertTrue(userPage.getContent().containsAll(List.of("e654321", "u123456")));
  }

  @Test
  void testWithAppTypesWithoutSboids() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(20), null,
        new HashSet<>(List.of(ApplicationType.TTFN, ApplicationType.LIDI)), null);
    Assertions.assertEquals(2, userPage.getTotalElements());
    Assertions.assertEquals(2, userPage.getContent().size());
    Assertions.assertTrue(userPage.getContent().containsAll(List.of("e654321", "u123456")));
  }

  @Test
  void testWithoutAppTypesWithSboids() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(20),
        new HashSet<>(List.of("ch:1:sboid:100", "ch:1:sboid:101")), null, null);
    Assertions.assertEquals(2, userPage.getTotalElements());
    Assertions.assertEquals(2, userPage.getContent().size());
    Assertions.assertTrue(userPage.getContent().containsAll(List.of("e654321", "u123456")));
  }

  @Test
  void testWithAppTypesWithSboids() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(20),
        new HashSet<>(List.of("ch:1:sboid:100", "ch:1:sboid:101")),
        new HashSet<>(List.of(ApplicationType.TTFN, ApplicationType.LIDI)), null);
    Assertions.assertEquals(1, userPage.getTotalElements());
    Assertions.assertEquals(1, userPage.getContent().size());
    Assertions.assertTrue(userPage.getContent().contains("e654321"));

    userPage = userAdministrationService.getUserPage(Pageable.ofSize(20), new HashSet<>(List.of("ch:1:sboid:102")),
        new HashSet<>(List.of(ApplicationType.TTFN)), null);
    Assertions.assertEquals(1, userPage.getTotalElements());
    Assertions.assertEquals(1, userPage.getContent().size());
    Assertions.assertTrue(userPage.getContent().contains("u123456"));
  }

  @Test
  void testPaging() {
    Page<String> userPage = userAdministrationService.getUserPage(Pageable.ofSize(1), new HashSet<>(List.of("ch:1:sboid:100")),
        new HashSet<>(List.of(ApplicationType.TTFN)), null);
    Assertions.assertEquals(2, userPage.getTotalElements());
    Assertions.assertEquals(1, userPage.getContent().size());
  }

}
