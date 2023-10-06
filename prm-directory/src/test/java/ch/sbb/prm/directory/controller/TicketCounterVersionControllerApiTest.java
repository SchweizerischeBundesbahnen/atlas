package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.TicketCounterTestData;
import ch.sbb.prm.directory.controller.model.create.CreateTicketCounterVersionModel;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import ch.sbb.prm.directory.service.RelationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

class TicketCounterVersionControllerApiTest extends BaseControllerApiTest {

  private final TicketCounterRepository ticketCounterRepository;
  private final StopPlaceRepository stopPlaceRepository;

  private final ReferencePointRepository referencePointRepository;

  @MockBean
  private final RelationService relationService;

  @Autowired
  TicketCounterVersionControllerApiTest(TicketCounterRepository ticketCounterRepository, StopPlaceRepository stopPlaceRepository,
      ReferencePointRepository referencePointRepository, RelationService relationService){
    this.ticketCounterRepository = ticketCounterRepository;
    this.stopPlaceRepository = stopPlaceRepository;
    this.referencePointRepository = referencePointRepository;
    this.relationService = relationService;
  }

  @BeforeEach()
  void initDB() {

  }

  @Test
  void shouldGetTicketCountersVersion() throws Exception {
    //given
    ticketCounterRepository.save(TicketCounterTestData.getTicketCounterVersion());

    //when
    mvc.perform(get("/v1/ticket-counters"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldCreateTicketCounter() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);

    CreateTicketCounterVersionModel model = TicketCounterTestData.getCreateTicketCounterVersionVersionModel();
    model.setParentServicePointSloid(parentServicePointSloid);

    //when && then
    mvc.perform(post("/v1/ticket-counters").contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isCreated());
    verify(relationService, times(1)).createRelation(any(RelationVersion.class));

  }

}
