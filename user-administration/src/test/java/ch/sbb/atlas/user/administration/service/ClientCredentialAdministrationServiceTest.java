package ch.sbb.atlas.user.administration.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.user.administration.CantonPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.ClientCredentialPermissionCreateModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.repository.ClientCredentialPermissionRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class ClientCredentialAdministrationServiceTest {

  @Autowired
  private ClientCredentialAdministrationService clientCredentialAdministrationService;

  @Autowired
  private ClientCredentialPermissionRepository clientCredentialPermissionRepository;

  @AfterEach
  void tearDown() {
    clientCredentialPermissionRepository.deleteAll();
  }

  @Test
  void shouldCreateClientCredentials() {
    ClientCredentialPermissionCreateModel client = ClientCredentialPermissionCreateModel.builder()
        .clientCredentialId("18746f30-7978-48b5-b19b-0f871fb12e67")
        .alias("Atlas Frontend Dev")
        .permissions(List.of(PermissionModel.builder()
            .role(ApplicationRole.SUPERVISOR)
            .application(ApplicationType.TIMETABLE_HEARING)
            .build()))
        .build();

    List<ClientCredentialPermission> savedPermissions = clientCredentialAdministrationService.create(client);
    assertThat(savedPermissions).isNotEmpty();
    ClientCredentialPermission savedPermission = savedPermissions.get(0);
    assertThat(savedPermission.getId()).isNotNull();
    assertThat(savedPermission.getPermissionRestrictions()).isEmpty();
  }

  @Test
  void shouldCreateClientCredentialsWithRestrictions() {
    ClientCredentialPermissionCreateModel client = ClientCredentialPermissionCreateModel.builder()
        .clientCredentialId("18746f30-7978-48b5-b19b-0f871fb12e67")
        .alias("Atlas Frontend Dev")
        .permissions(List.of(PermissionModel.builder()
            .role(ApplicationRole.SUPERVISOR)
            .application(ApplicationType.TIMETABLE_HEARING)
            .permissionRestrictions(List.of(new CantonPermissionRestrictionModel(SwissCanton.BERN)))
            .build()))
        .build();

    List<ClientCredentialPermission> savedPermissions = clientCredentialAdministrationService.create(client);
    assertThat(savedPermissions).isNotEmpty();
    ClientCredentialPermission savedPermission = savedPermissions.get(0);
    assertThat(savedPermission.getId()).isNotNull();
    assertThat(savedPermission.getPermissionRestrictions()).hasSize(1);
    assertThat(savedPermission.getPermissionRestrictions().iterator().next().getId()).isNotNull();
  }
}