package ch.sbb.atlas.user.administration.module.clientcredential.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.user.administration.CantonPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.ClientCredentialCreateModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationRole;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.module.clientcredential.entity.ClientCredentialPermission;
import ch.sbb.atlas.user.administration.module.clientcredential.repository.ClientCredentialPermissionRepository;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class ClientCredentialAdministrationServiceTest {

  public static final String CLIENT_CREDENTIAL_ID = "18746f30-7978-48b5-b19b-0f871fb12e67";
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
    ClientCredentialCreateModel client = ClientCredentialCreateModel.builder()
        .clientCredentialId(CLIENT_CREDENTIAL_ID)
        .alias("Atlas Frontend Dev")
        .build();

    List<ClientCredentialPermission> savedPermissions = clientCredentialAdministrationService.create(client);
    assertThat(savedPermissions).isNotEmpty();
    ClientCredentialPermission savedPermission = savedPermissions.getFirst();
    assertThat(savedPermission.getId()).isNotNull();
    assertThat(savedPermission.getPermissionRestrictions()).isEmpty();
  }

  @Test
  void shouldUpdateClientCredentialsWithRestrictions() {
    // Given
    ClientCredentialCreateModel client = ClientCredentialCreateModel.builder()
        .clientCredentialId(CLIENT_CREDENTIAL_ID)
        .alias("Atlas Frontend Dev")
        .build();
    clientCredentialAdministrationService.create(client);

    PermissionModel permission = PermissionModel.builder()
        .role(ApplicationRole.WRITER)
        .application(ApplicationType.TIMETABLE_HEARING)
        .permissionRestrictions(List.of(new CantonPermissionRestrictionModel(SwissCanton.URI),
            new CantonPermissionRestrictionModel(SwissCanton.JURA)))
        .build();

    // When
    clientCredentialAdministrationService.update(CLIENT_CREDENTIAL_ID, ApplicationType.TIMETABLE_HEARING, permission);
    List<ClientCredentialPermission> savedPermissions =
        clientCredentialAdministrationService.getClientCredentialPermission(CLIENT_CREDENTIAL_ID);
    assertThat(savedPermissions).isNotEmpty();
    ClientCredentialPermission savedPermission =
        savedPermissions.stream().filter(i -> i.getApplication() == ApplicationType.TIMETABLE_HEARING).findFirst().orElseThrow();
    assertThat(savedPermission.getRole()).isEqualTo(ApplicationRole.WRITER);
    assertThat(savedPermission.getPermissionRestrictions()).hasSize(2);
  }

}