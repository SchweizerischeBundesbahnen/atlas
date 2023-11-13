package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.stoppoint.CreateStopPointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class StopPointVersionControllerApiTest extends BaseControllerApiTest {

  private final SharedServicePointRepository sharedServicePointRepository;

  private final StopPointRepository stopPointRepository;

  @Autowired
  StopPointVersionControllerApiTest(SharedServicePointRepository sharedServicePointRepository, StopPointRepository stopPointRepository) {
    this.sharedServicePointRepository = sharedServicePointRepository;
    this.stopPointRepository = stopPointRepository;
  }

  @Test
  void shouldGetStopPointsVersionWithoutFilter() throws Exception {
    //given
    StopPointVersion version = stopPointRepository.save(StopPointTestData.getStopPointVersion());
    //when & then
    mvc.perform(get("/v1/stop-points"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].id", is(version.getId().intValue())))
        .andExpect(jsonPath("$.objects[0].number.number", is(version.getNumber().getNumber())));
  }

  @Test
  void shouldGetStopPointVersionsWithFilter() throws Exception {
    //given
    StopPointVersion version = stopPointRepository.save(StopPointTestData.getStopPointVersion());
    //when & then
    mvc.perform(get("/v1/stop-points" +
            "?numbers=1234567" +
            "&sloids=ch:1:sloid:12345" +
            "&fromDate=" + version.getValidFrom() +
            "&toDate=" + version.getValidTo()+
            "&validOn=" + LocalDate.of(2000, 6, 28) +
            "&createdAfter=" + version.getCreationDate().minusSeconds(1).format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)) +
            "&modifiedAfter=" + version.getEditionDate().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN))
            ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].id" , is(version.getId().intValue())));
  }

  @Test
  void shouldGetStopPointVersionsWithArrayInFilter() throws Exception {
    //given
    StopPointVersion version = stopPointRepository.save(StopPointTestData.getStopPointVersion());
    //when & then
    mvc.perform(get("/v1/stop-points" +
            "?numbers=1234567&numbers=1000000" +
            "&sloids=ch:1:sloid:12345&sloids=ch:1:sloid:54321" +
            "&fromDate=" + version.getValidFrom() +
            "&toDate=" + version.getValidTo()+
            "&validOn=" + LocalDate.of(2000, 6, 28) +
            "&createdAfter=" + version.getCreationDate().minusSeconds(1).format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)) +
            "&modifiedAfter=" + version.getEditionDate().format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN))
            ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].id" , is(version.getId().intValue())));
  }

  @Test
  void shouldNotGetStopPointVersionsWithFilter() throws Exception {
    //given
    stopPointRepository.save(StopPointTestData.getStopPointVersion());
    //when
    mvc.perform(get("/v1/stop-points?numbers=1000000"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(0)));
  }
  @Test
  void shouldCreateStopPoint() throws Exception {
    //given
    CreateStopPointVersionModel stopPointCreateVersionModel = StopPointTestData.getStopPointCreateVersionModel();
    SharedServicePoint servicePoint = SharedServicePoint.builder()
        .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:7000\",\"sboids\":[\"ch:1:sboid:100602\"],\"trafficPointSloids\":[]}")
        .sloid("ch:1:sloid:7000")
        .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);
    //when && then
    mvc.perform(post("/v1/stop-points").contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCreateVersionModel)))
        .andExpect(status().isCreated());

  }

  @Test
  void shouldNotCreateStopPointReducedIfCompletePropertiesProvided() throws Exception {
    //given
    CreateStopPointVersionModel stopPointCreateVersionModel = StopPointTestData.getWrongStopPointReducedCreateVersionModel();
    SharedServicePoint servicePoint = SharedServicePoint.builder()
        .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:7000\",\"sboids\":[\"ch:1:sboid:100602\"],\"trafficPointSloids\":[]}")
        .sloid("ch:1:sloid:7000")
        .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);
    //when && then
    mvc.perform(post("/v1/stop-points").contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCreateVersionModel)))
        .andExpect(status().isBadRequest());

  }

  @Test
  void shouldNotCreateStopPointWhenServicePointDoesNotExists() throws Exception {
    //given
    CreateStopPointVersionModel stopPointCreateVersionModel = StopPointTestData.getStopPointCreateVersionModel();
    //when && then
    mvc.perform(post("/v1/stop-points").contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCreateVersionModel)))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("The service point with sloid ch:1:sloid:7000 does not exists.")));

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
  void shouldUpdateStopPoint() throws Exception {
    //given
    StopPointVersion version1 = stopPointRepository.saveAndFlush(StopPointTestData.builderVersion1().build());
    StopPointVersion version2 = stopPointRepository.saveAndFlush(StopPointTestData.builderVersion2().build());

    CreateStopPointVersionModel editedVersionModel = new CreateStopPointVersionModel();
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
    editedVersionModel.setAssistanceCondition(version2.getAssistanceCondition());
    editedVersionModel.setAssistanceService(version2.getAssistanceService());
    editedVersionModel.setAudioTicketMachine(version2.getAudioTicketMachine());
    editedVersionModel.setAdditionalInformation(version2.getAdditionalInformation());
    editedVersionModel.setDynamicAudioSystem(version2.getDynamicAudioSystem());
    editedVersionModel.setDynamicOpticSystem(version2.getDynamicOpticSystem());
    editedVersionModel.setInfoTicketMachine(version2.getInfoTicketMachine());
    editedVersionModel.setAdditionalInformation(version2.getAdditionalInformation());
    editedVersionModel.setInteroperable(version2.getInteroperable());
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
    SharedServicePoint servicePoint = SharedServicePoint.builder()
        .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:12345\",\"sboids\":[\"ch:1:sboid:100602\"],\"trafficPointSloids\":[]}")
        .sloid("ch:1:sloid:12345")
        .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);

    //when && then
    mvc.perform(put("/v1/stop-points/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")));

  }

}
