package ch.sbb.business.organisation.directory.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class CrdClientTest {

  @Autowired
  private CrdClient crdClient;

  //TODO: remove this external URL call
  //TODO: setup openshift secret
  @Test
  void shouldCallSoapApi() {
    List<Company> companies = crdClient.getAllCompanies();
    assertThat(companies).isNotEmpty();
  }
}