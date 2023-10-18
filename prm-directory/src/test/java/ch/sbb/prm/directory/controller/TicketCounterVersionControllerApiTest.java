package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.prm.model.ticketcounter.CreateTicketCounterVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.ReferencePointTestData;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.prm.directory.TicketCounterTestData;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.RelationVersion;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import ch.sbb.prm.directory.repository.ReferencePointRepository;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.repository.TicketCounterRepository;
import ch.sbb.prm.directory.service.RelationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
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
    verify(relationService, times(1)).save(any(RelationVersion.class));

  }

  @Test
  void shouldNotCreateTicketCounterWhenStopPlaceDoesNotExists() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);

    CreateTicketCounterVersionModel model = TicketCounterTestData.getCreateTicketCounterVersionVersionModel();
    model.setParentServicePointSloid(parentServicePointSloid);

    //when && then
    mvc.perform(post("/v1/ticket-counters").contentType(contentType)
            .content(mapper.writeValueAsString(model)))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("The stop place with sloid ch:1:sloid:7000 does not exists.")));
    verify(relationService, times(0)).save(any(RelationVersion.class));

  }
  /**
   * Szenario 8a: Letzte Version terminieren wenn nur validTo ist updated
   * NEU:      |______________________|
   * IST:      |-------------------------------------------------------
   * Version:                            1
   *
   * RESULTAT: |----------------------| Version wird per xx aufgehoben
   * Version:         1
   */
  @Test
  void shouldUpdateTicketCounter() throws Exception {
    //given
    String parentServicePointSloid = "ch:1:sloid:7000";
    StopPlaceVersion stopPlaceVersion = StopPlaceTestData.getStopPlaceVersion();
    stopPlaceVersion.setSloid(parentServicePointSloid);
    stopPlaceRepository.save(stopPlaceVersion);
    ReferencePointVersion referencePointVersion = ReferencePointTestData.getReferencePointVersion();
    referencePointVersion.setParentServicePointSloid(parentServicePointSloid);
    referencePointRepository.save(referencePointVersion);

    TicketCounterVersion version1 = TicketCounterTestData.builderVersion1().build();
    version1.setParentServicePointSloid(parentServicePointSloid);
    ticketCounterRepository.saveAndFlush(version1);
    TicketCounterVersion version2 = TicketCounterTestData.builderVersion2().build();
    version2.setParentServicePointSloid(parentServicePointSloid);
    ticketCounterRepository.saveAndFlush(version2);

    CreateTicketCounterVersionModel editedVersionModel = new CreateTicketCounterVersionModel();
    editedVersionModel.setParentServicePointSloid(parentServicePointSloid);
    editedVersionModel.setSloid(version2.getSloid());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setNumberWithoutCheckDigit(version2.getNumber().getNumber());
    editedVersionModel.setDesignation(version2.getDesignation());
    editedVersionModel.setAdditionalInformation(version2.getAdditionalInformation());
    editedVersionModel.setInductionLoop(version2.getInductionLoop());
    editedVersionModel.setOpeningHours(version2.getOpeningHours());
    editedVersionModel.setWheelchairAccess(version2.getWheelchairAccess());
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());

    //when & then
    mvc.perform(put("/v1/ticket-counters/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")));

  }


}
