package ch.sbb.atlas.user.administration.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.api.user.administration.CantonPermissionRestrictionModel;
import ch.sbb.atlas.api.user.administration.ClientCredentialCreateModel;
import ch.sbb.atlas.api.user.administration.PermissionModel;
import ch.sbb.atlas.api.user.administration.SboidPermissionRestrictionModel;
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
 class ClientCredentialAdministrationServiceTest {

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
    ClientCredentialCreateModel client = ClientCredentialCreateModel.builder()
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

  @Test
  void shouldUpdateClientCredentialsWithRestrictions() {
    // Given
    ClientCredentialCreateModel client = ClientCredentialCreateModel.builder()
        .clientCredentialId("18746f30-7978-48b5-b19b-0f871fb12e67")
        .alias("Atlas Frontend Dev")
        .permissions(List.of(PermissionModel.builder()
            .role(ApplicationRole.SUPERVISOR)
            .application(ApplicationType.TIMETABLE_HEARING)
            .permissionRestrictions(List.of(new CantonPermissionRestrictionModel(SwissCanton.BERN)))
            .build()))
        .build();
    clientCredentialAdministrationService.create(client);

    ClientCredentialCreateModel clientUpdate = ClientCredentialCreateModel.builder()
        .clientCredentialId("18746f30-7978-48b5-b19b-0f871fb12e67")
        .alias("Atlas Frontend Dev")
        .permissions(List.of(PermissionModel.builder()
            .role(ApplicationRole.WRITER)
            .application(ApplicationType.TIMETABLE_HEARING)
            .permissionRestrictions(List.of(new CantonPermissionRestrictionModel(SwissCanton.URI),
                new CantonPermissionRestrictionModel(SwissCanton.JURA)))
            .build()))
        .build();

    // When
    clientCredentialAdministrationService.update(clientUpdate.getClientCredentialId(), clientUpdate);
    List<ClientCredentialPermission> savedPermissions =
        clientCredentialAdministrationService.getClientCredentialPermission(
            clientUpdate.getClientCredentialId());
    assertThat(savedPermissions).isNotEmpty();
    ClientCredentialPermission savedPermission = savedPermissions.get(0);
    assertThat(savedPermission.getRole()).isEqualTo(ApplicationRole.WRITER);
    assertThat(savedPermission.getPermissionRestrictions()).hasSize(2);
  }

  @Test
  void shouldUpdateClientCredentialsWithAdditionalRestrictions() {
    // Given
    ClientCredentialCreateModel client = ClientCredentialCreateModel.builder()
        .clientCredentialId("18746f30-7978-48b5-b19b-0f871fb12e67")
        .alias("Atlas Frontend Dev")
        .permissions(List.of(PermissionModel.builder()
            .role(ApplicationRole.SUPERVISOR)
            .application(ApplicationType.TIMETABLE_HEARING)
            .permissionRestrictions(List.of(new CantonPermissionRestrictionModel(SwissCanton.BERN)))
            .build()))
        .build();
    clientCredentialAdministrationService.create(client);

    ClientCredentialCreateModel clientUpdate = ClientCredentialCreateModel.builder()
        .clientCredentialId("18746f30-7978-48b5-b19b-0f871fb12e67")
        .alias("Atlas Frontend Dev")
        .permissions(List.of(PermissionModel.builder()
                .role(ApplicationRole.WRITER)
                .application(ApplicationType.TIMETABLE_HEARING)
                .permissionRestrictions(List.of(new CantonPermissionRestrictionModel(SwissCanton.URI),
                    new CantonPermissionRestrictionModel(SwissCanton.JURA)))
                .build(),
            PermissionModel.builder()
                .role(ApplicationRole.WRITER)
                .application(ApplicationType.LIDI)
                .permissionRestrictions(List.of(new SboidPermissionRestrictionModel("ch:1:sboid:134123"),
                    new SboidPermissionRestrictionModel("ch:1:sboid:234211")))
                .build()))
        .build();

    // When
    clientCredentialAdministrationService.update(clientUpdate.getClientCredentialId(), clientUpdate);
    List<ClientCredentialPermission> savedPermissions =
        clientCredentialAdministrationService.getClientCredentialPermission(
            clientUpdate.getClientCredentialId());
    assertThat(savedPermissions).isNotEmpty();

    ClientCredentialPermission savedPermission =
        savedPermissions.stream().filter(i -> i.getApplication()==ApplicationType.TIMETABLE_HEARING).findFirst().orElseThrow();
    assertThat(savedPermission.getRole()).isEqualTo(ApplicationRole.WRITER);
    assertThat(savedPermission.getApplication()).isEqualTo(ApplicationType.TIMETABLE_HEARING);
    assertThat(savedPermission.getPermissionRestrictions()).hasSize(2);

    savedPermission =
        savedPermissions.stream().filter(i -> i.getApplication()==ApplicationType.LIDI).findFirst().orElseThrow();
    assertThat(savedPermission.getRole()).isEqualTo(ApplicationRole.WRITER);
    assertThat(savedPermission.getApplication()).isEqualTo(ApplicationType.LIDI);
    assertThat(savedPermission.getPermissionRestrictions()).hasSize(2);
  }
}