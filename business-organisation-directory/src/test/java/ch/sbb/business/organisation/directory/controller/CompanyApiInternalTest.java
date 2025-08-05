package ch.sbb.business.organisation.directory.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.business.organisation.directory.entity.Company;
import ch.sbb.business.organisation.directory.entity.Company.Fields;
import ch.sbb.business.organisation.directory.repository.CompanyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CompanyApiInternalTest extends BaseControllerApiTest {

  private final CompanyRepository repository;

  @Autowired
  CompanyApiInternalTest(CompanyRepository repository) {
    this.repository = repository;
  }

  @BeforeEach
  void createDefaultVersion() {
    repository.save(Company.builder()
        .uicCode("5")
        .name("Beste Company")
        .shortName("ShortName")
        .countryCodeIso("CH")
        .build());
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  @Test
  void shouldGetCompanies() throws Exception {
    mvc.perform(get("/internal/companies")).andExpect(status().isOk())
        .andExpect(jsonPath("$.objects[0]." + Fields.uicCode, is("5")))
        .andExpect(jsonPath("$.objects[0]." + Fields.name, is("Beste Company")));
  }

  @Test
  void shouldGetCompany() throws Exception {
    mvc.perform(get("/internal/companies/5")).andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.uicCode, is("5")))
        .andExpect(jsonPath("$." + Fields.name, is("Beste Company")));
  }
}
