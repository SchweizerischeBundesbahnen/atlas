package ch.sbb.prm.directory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.prm.directory.TicketCounterTestData;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class TicketCounterRepositoryTest {

  private final TicketCounterRepository ticketCounterRepository;

  @Autowired
  TicketCounterRepositoryTest(TicketCounterRepository ticketCounterRepository) {
    this.ticketCounterRepository = ticketCounterRepository;
  }

  @BeforeEach()
  void initDB() {
    ticketCounterRepository.save(TicketCounterTestData.getTicketCounterversion());
  }

  @AfterEach
  void tearDown() {
    ticketCounterRepository.deleteAll();
  }

  @Test
  void shouldReturnStopPlaces() {
    //when
   List<TicketCounterVersion> result = ticketCounterRepository.findAll();
   //then
   assertThat(result).hasSize(1);
  }

}