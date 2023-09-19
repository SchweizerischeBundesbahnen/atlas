package ch.sbb.business.organisation.directory.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompany.Fields;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

 class TransportCompanyControllerApiTest extends BaseControllerApiTest {

  @Autowired
  private TransportCompanyRepository repository;

  @BeforeEach
  void createDefaultVersion() {
    repository.save(TransportCompany.builder()
                                    .id(5L)
                                    .description("Beste Company")
                                    .number("#0001")
                                    .enterpriseId("enterprisige ID")
                                    .build());
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  @Test
  void shouldGetTransportCompanies() throws Exception {
    mvc.perform(get("/v1/transport-companies")).andExpect(status().isOk())
       .andExpect(jsonPath("$.objects[0]." + Fields.id, is(5)))
       .andExpect(jsonPath("$.objects[0]." + Fields.description, is("Beste Company")))
       .andExpect(jsonPath("$.objects[0]." + Fields.number, is("#0001")));
  }

  @Test
  void shouldGetTransportCompany() throws Exception {
    mvc.perform(get("/v1/transport-companies/5")).andExpect(status().isOk())
       .andExpect(jsonPath("$." + Fields.id, is(5)))
       .andExpect(jsonPath("$." + Fields.description, is("Beste Company")))
       .andExpect(jsonPath("$." + Fields.number, is("#0001")));
  }
}