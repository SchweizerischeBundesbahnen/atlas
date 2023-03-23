package ch.sbb.atlas.user.administration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.user.administration.entity.ClientCredentialPermission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class ClientCredentialAdministrationServiceTest {

  @Autowired
  private ClientCredentialAdministrationService clientCredentialAdministrationService;

  @Test
  void shouldCreateClientCredentials() {
    ClientCredentialPermission client = ClientCredentialPermission.builder()
        .clientCredentialId("18746f30-7978-48b5-b19b-0f871fb12e67")
        .alias("Atlas Frontend Dev")
        .build();

    ClientCredentialPermission savedPermission = clientCredentialAdministrationService.save(client);
    assertThat(savedPermission.getId()).isNotNull();
  }
}