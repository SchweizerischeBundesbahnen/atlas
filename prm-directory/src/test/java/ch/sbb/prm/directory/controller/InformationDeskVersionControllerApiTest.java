package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.InformationDeskTestData;
import ch.sbb.prm.directory.repository.InformationDeskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class InformationDeskVersionControllerApiTest extends BaseControllerApiTest {

  private final InformationDeskRepository informationDeskRepository;

  @Autowired
  InformationDeskVersionControllerApiTest(InformationDeskRepository informationDeskRepository){
    this.informationDeskRepository = informationDeskRepository;
  }

  @BeforeEach()
  void initDB() {
    informationDeskRepository.save(InformationDeskTestData.getInformationDeskVersion());
  }

  @Test
  void shouldGetTicketCountersVersion() throws Exception {
    mvc.perform(get("/v1/information-desks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

}
