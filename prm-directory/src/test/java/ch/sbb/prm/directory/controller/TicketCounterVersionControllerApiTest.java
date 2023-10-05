package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.TicketCounterTestData;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TicketCounterVersionControllerApiTest extends BaseControllerApiTest {

  private final TicketCounterRepository ticketCounterRepository;

  @Autowired
  TicketCounterVersionControllerApiTest(TicketCounterRepository ticketCounterRepository){
    this.ticketCounterRepository = ticketCounterRepository;
  }

  @BeforeEach()
  void initDB() {
    ticketCounterRepository.save(TicketCounterTestData.getTicketCounterVersion());
  }

  @AfterEach
  void tearDown() {
    ticketCounterRepository.deleteAll();
  }

  @Test
  void shouldGetTicketCountersVersion() throws Exception {
    mvc.perform(get("/v1/ticket-counters"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

}
