package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.StopPlaceTestData;
import ch.sbb.atlas.api.prm.model.stopplace.CreateStopPlaceVersionModel;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import ch.sbb.prm.directory.repository.StopPlaceRepository;
import ch.sbb.prm.directory.service.SharedServicePointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class StopPlaceVersionControllerApiTest extends BaseControllerApiTest {

  @MockBean
  private SharedServicePointService sharedServicePointService;

  private final StopPlaceRepository stopPlaceRepository;

  @Autowired
  StopPlaceVersionControllerApiTest(StopPlaceRepository stopPlaceRepository) {
    this.stopPlaceRepository = stopPlaceRepository;
  }

  @Test
  void shouldGetStopPlacesVersion() throws Exception {
    //given
    stopPlaceRepository.save(StopPlaceTestData.getStopPlaceVersion());
    //when & then
    mvc.perform(get("/v1/stop-places"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldCreateStopPlace() throws Exception {
    //given
    CreateStopPlaceVersionModel stopPlaceCreateVersionModel = StopPlaceTestData.getStopPlaceCreateVersionModel();
    //when && then
    mvc.perform(post("/v1/stop-places").contentType(contentType)
            .content(mapper.writeValueAsString(stopPlaceCreateVersionModel)))
        .andExpect(status().isCreated());

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
  void shouldUpdateStopPlace() throws Exception {
    //given
    StopPlaceVersion version1 = stopPlaceRepository.saveAndFlush(StopPlaceTestData.builderVersion1().build());
    StopPlaceVersion version2 = stopPlaceRepository.saveAndFlush(StopPlaceTestData.builderVersion2().build());

    CreateStopPlaceVersionModel editedVersionModel = new CreateStopPlaceVersionModel();
    editedVersionModel.setNumberWithoutCheckDigit(version2.getNumber().getNumber());
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setFreeText(version2.getFreeText());
    editedVersionModel.setMeansOfTransport(version2.getMeansOfTransport().stream().toList());
    editedVersionModel.setAddress(version2.getAddress());
    editedVersionModel.setZipCode(version2.getZipCode());
    editedVersionModel.setCity(version2.getCity());
    editedVersionModel.setAlternativeTransport(version2.getAlternativeTransport());
    editedVersionModel.setAlternativeTransportCondition(version2.getAlternativeTransportCondition());
    editedVersionModel.setAssistanceAvailability(version2.getAssistanceAvailability());
    editedVersionModel.setAlternativeCondition(version2.getAlternativeCondition());
    editedVersionModel.setAssistanceService(version2.getAssistanceService());
    editedVersionModel.setAudioTicketMachine(version2.getAudioTicketMachine());
    editedVersionModel.setAdditionalInfo(version2.getAdditionalInfo());
    editedVersionModel.setDynamicAudioSystem(version2.getDynamicAudioSystem());
    editedVersionModel.setDynamicOpticSystem(version2.getDynamicOpticSystem());
    editedVersionModel.setInfoTicketMachine(version2.getInfoTicketMachine());
    editedVersionModel.setAdditionalInfo(version2.getAdditionalInfo());
    editedVersionModel.setInteroperable(version2.isInteroperable());
    editedVersionModel.setUrl(version2.getUrl());
    editedVersionModel.setVisualInfo(version2.getVisualInfo());
    editedVersionModel.setWheelchairTicketMachine(version2.getWheelchairTicketMachine());
    editedVersionModel.setAssistanceRequestFulfilled(version2.getAssistanceRequestFulfilled());
    editedVersionModel.setTicketMachine(version2.getTicketMachine());
    editedVersionModel.setCreationDate(version2.getCreationDate());
    editedVersionModel.setEditionDate(version2.getEditionDate());
    editedVersionModel.setCreator(version2.getCreator());
    editedVersionModel.setEditor(version2.getEditor());
    editedVersionModel.setEtagVersion(version2.getVersion());


    //when && then
    mvc.perform(put("/v1/stop-places/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")));

  }

}
