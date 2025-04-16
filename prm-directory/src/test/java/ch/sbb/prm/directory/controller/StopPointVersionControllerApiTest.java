package ch.sbb.prm.directory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.prm.model.stoppoint.StopPointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.SharedServicePointTestData;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.SharedServicePoint;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.repository.SharedServicePointRepository;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.service.PrmChangeRecordingVariantService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class StopPointVersionControllerApiTest extends BaseControllerApiTest {

  private final SharedServicePointRepository sharedServicePointRepository;
  private final StopPointRepository stopPointRepository;

  @MockitoBean
  private final PrmChangeRecordingVariantService prmChangeRecordingVariantService;

  @Autowired
  StopPointVersionControllerApiTest(SharedServicePointRepository sharedServicePointRepository,
      StopPointRepository stopPointRepository,
      PrmChangeRecordingVariantService prmChangeRecordingVariantService) {
    this.sharedServicePointRepository = sharedServicePointRepository;
    this.stopPointRepository = stopPointRepository;
    this.prmChangeRecordingVariantService = prmChangeRecordingVariantService;
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
        .andExpect(jsonPath("$.objects[0].number.number", is(version.getNumber().getNumber())))
        .andExpect(jsonPath("$.objects[0]." + StopPointVersion.Fields.status, is(Status.VALIDATED.name())));
  }

  @Test
  void shouldGetStopPointVersionsWithFilter() throws Exception {
    //given
    StopPointVersion version = stopPointRepository.saveAndFlush(StopPointTestData.getStopPointVersion());
    //when & then
    mvc.perform(get("/v1/stop-points" +
            "?numbers=1234567" +
            "&sloids=ch:1:sloid:12345" +
            "&statusRestrictions=VALIDATED" +
            "&fromDate=" + version.getValidFrom() +
            "&toDate=" + version.getValidTo() +
            "&validOn=" + LocalDate.of(2000, 6, 28) +
            "&createdAfter=" + version.getCreationDate().minusSeconds(1)
            .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)) +
            "&modifiedAfter=" + version.getEditionDate()
            .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN))
        ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].id", is(version.getId().intValue())));
  }

  @Test
  void shouldGetStopPointVersionsWithArrayInFilter() throws Exception {
    //given
    StopPointVersion version = stopPointRepository.saveAndFlush(StopPointTestData.getStopPointVersion());
    //when & then
    mvc.perform(get("/v1/stop-points" +
            "?numbers=1234567&numbers=1000000" +
            "&sloids=ch:1:sloid:12345&sloids=ch:1:sloid:54321" +
            "&fromDate=" + version.getValidFrom() +
            "&toDate=" + version.getValidTo() +
            "&validOn=" + LocalDate.of(2000, 6, 28) +
            "&createdAfter=" + version.getCreationDate().minusSeconds(1)
            .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN)) +
            "&modifiedAfter=" + version.getEditionDate()
            .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN))
        ))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)))
        .andExpect(jsonPath("$.objects[0].id", is(version.getId().intValue())));
  }

  @Test
  void shouldGetStopPointVersionsWithValidToFromDateFilter() throws Exception {
    //given
    StopPointVersion stopPoint = StopPointTestData.getStopPointVersion();
    stopPoint.setValidFrom(LocalDate.of(2001, 1, 1));
    stopPoint.setValidTo(LocalDate.of(2001, 12, 31));
    stopPoint.setFreeText("Another free text.");
    stopPointRepository.save(StopPointTestData.getStopPointVersion());
    stopPointRepository.save(stopPoint);
    //when & then
    mvc.perform(get("/v1/stop-points?validToFromDate=" + stopPoint.getValidFrom()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)));
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
    StopPointVersionModel stopPointCreateVersionModel = StopPointTestData.getStopPointCreateVersionModel();
    SharedServicePoint servicePoint = SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:7000",
        Set.of("ch:1:sboid:100602"),
        Collections.emptySet());
    sharedServicePointRepository.saveAndFlush(servicePoint);
    //when && then
    mvc.perform(post("/v1/stop-points").contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCreateVersionModel)))
        .andExpect(status().isCreated());
  }

  @Test
  void shouldNotCreateStopPointReducedIfCompletePropertiesProvided() throws Exception {
    //given
    StopPointVersionModel stopPointCreateVersionModel = StopPointTestData.getWrongStopPointReducedCreateVersionModel();
    SharedServicePoint servicePoint =
        SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:7000", Set.of("ch:1:sboid:100602"),
            Collections.emptySet());

    sharedServicePointRepository.saveAndFlush(servicePoint);
    //when && then
    mvc.perform(post("/v1/stop-points").contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCreateVersionModel)))
        .andExpect(status().isBadRequest());

  }

  @Test
  void shouldNotCreateStopPointCompleteWithNotValidatableProperties() throws Exception {
    //given
    StopPointVersionModel stopPointCreateVersionModel =
        StopPointTestData.getCompleteNotValidatableStopPointReducedCreateVersionModel();
    SharedServicePoint servicePoint = SharedServicePoint.builder()
        .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:7000\",\"sboids\":[\"ch:1:sboid:100602\"],\"trafficPointSloids\":[]}")
        .sloid("ch:1:sloid:7000")
        .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);
    //when && then
    mvc.perform(post("/v1/stop-points").contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCreateVersionModel)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details.size()", is(9)));

  }

  @Test
  void shouldNotCreateStopPointIsAlreadyExists() throws Exception {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointRepository.save(stopPointVersion);
    StopPointVersionModel stopPointCreateVersionModel = StopPointTestData.getStopPointCreateVersionModel();
    stopPointCreateVersionModel.setSloid(stopPointVersion.getSloid());
    SharedServicePoint servicePoint = SharedServicePoint.builder()
        .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:12345\",\"sboids\":[\"ch:1:sboid:100602\"],\"trafficPointSloids\":[]}")
        .sloid("ch:1:sloid:12345")
        .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);
    //when && then
    mvc.perform(post("/v1/stop-points").contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCreateVersionModel)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message", is("The stop point with sloid ch:1:sloid:12345 already exists.")));

  }

  @Test
  void shouldNotCreateStopPointReducedIfServicePointHasAsCountryNotSwiss() throws Exception {
    //given
    StopPointVersionModel stopPointCreateVersionModel = StopPointTestData.getStopPointCreateVersionModel();
    stopPointCreateVersionModel.setSloid("ch:1:sloid:1101407");
    SharedServicePoint servicePoint = SharedServicePoint.builder()
        .servicePoint(
            "{\"servicePointSloid\":\"ch:1:sloid:1101407\",\"sboids\":[\"ch:1:sboid:100602\"],\"trafficPointSloids\":[]}")
        .sloid("ch:1:sloid:1101407")
        .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);
    //when && then
    mvc.perform(post("/v1/stop-points").contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCreateVersionModel)))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("PRM does not allow to create StopPoints from non-Swiss ServicePoints!")))
        .andExpect(jsonPath("$.error", is("The given ServicePointNumber 1101407 has GERMANY_BUS as its Country!")));

  }

  @Test
  void shouldNotCreateStopPointVersionWhenServicePointDoesNotExist() throws Exception {
    //given
    StopPointVersionModel stopPointCreateVersionModel = StopPointTestData.getStopPointCreateVersionModel();
    SharedServicePoint servicePoint = SharedServicePoint.builder()
        .servicePoint("{\"servicePointSloid\":\"ch:1:sloid:7001\",\"sboids\":[\"ch:1:sboid:100602\"],\"trafficPointSloids\":[]}")
        .sloid("ch:1:sloid:7001")
        .build();
    sharedServicePointRepository.saveAndFlush(servicePoint);

    //when && then
    mvc.perform(post("/v1/stop-points").contentType(contentType)
            .content(mapper.writeValueAsString(stopPointCreateVersionModel)))
        .andExpect(status().isPreconditionFailed())
        .andExpect(jsonPath("$.message", is("The service point with sloid ch:1:sloid:7000 does not exist.")));
  }

  /**
   * Szenario 8a: Letzte Version terminieren wenn nur validTo ist updated
   * NEU:      |______________________|
   * IST:      |-------------------------------------------------------
   * Version:                            1
   * RESULTAT: |----------------------| Version wird per xx aufgehoben
   * Version:         1
   */
  @Test
  void shouldUpdateStopPoint() throws Exception {
    //given
    StopPointVersion version1 =
        stopPointRepository.saveAndFlush(StopPointTestData.builderVersion1().build());
    StopPointVersion version2 = stopPointRepository.saveAndFlush(StopPointTestData.builderVersion2().build());

    StopPointVersionModel editedVersionModel = new StopPointVersionModel();
    editedVersionModel.setValidFrom(version2.getValidFrom());
    editedVersionModel.setValidTo(version2.getValidTo().minusYears(1));
    editedVersionModel.setFreeText(version2.getFreeText());
    editedVersionModel.setMeansOfTransport(version2.getMeansOfTransport().stream().toList());
    editedVersionModel.setAddress(version2.getAddress());
    editedVersionModel.setZipCode(version2.getZipCode());
    editedVersionModel.setCity(version2.getCity());
    editedVersionModel.setAlternativeTransport(version2.getAlternativeTransport());
    editedVersionModel.setShuttleService(version2.getShuttleService());
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
    editedVersionModel.setSloid("ch:1:sloid:12345");

    SharedServicePoint servicePoint = SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:12345", Set.of("ch:1:sboid"
        + ":100602"), Collections.emptySet());
    sharedServicePointRepository.saveAndFlush(servicePoint);

    //when && then
    mvc.perform(put("/v1/stop-points/" + version2.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2000-01-01")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2000-12-31")))
        .andExpect(jsonPath("$[0]." + StopPointVersion.Fields.status, is(Status.VALIDATED.name())))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2001-01-01")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2001-12-31")))
        .andExpect(jsonPath("$[1]." + StopPointVersion.Fields.status, is(Status.VALIDATED.name())));
  }

  @Test
  void shouldChangeStopPointVariant() throws Exception {
    //given
    StopPointVersion version1 = stopPointRepository.saveAndFlush(StopPointTestData.builderVersion2()
        .meansOfTransport(Set.of(MeanOfTransport.TRAIN, MeanOfTransport.METRO))
        .build());

    StopPointVersionModel editedVersionModel = new StopPointVersionModel();
    editedVersionModel.setValidFrom(version1.getValidFrom());
    editedVersionModel.setValidTo(version1.getValidTo().minusYears(1));
    editedVersionModel.setFreeText(version1.getFreeText());
    editedVersionModel.setMeansOfTransport(List.of(MeanOfTransport.BUS));
    editedVersionModel.setAddress(version1.getAddress());
    editedVersionModel.setZipCode(version1.getZipCode());
    editedVersionModel.setCity(version1.getCity());
    editedVersionModel.setAlternativeTransport(version1.getAlternativeTransport());
    editedVersionModel.setAlternativeTransportCondition(version1.getAlternativeTransportCondition());
    editedVersionModel.setAssistanceAvailability(version1.getAssistanceAvailability());
    editedVersionModel.setAssistanceCondition(version1.getAssistanceCondition());
    editedVersionModel.setAssistanceService(version1.getAssistanceService());
    editedVersionModel.setAudioTicketMachine(version1.getAudioTicketMachine());
    editedVersionModel.setAdditionalInformation(version1.getAdditionalInformation());
    editedVersionModel.setDynamicAudioSystem(version1.getDynamicAudioSystem());
    editedVersionModel.setDynamicOpticSystem(version1.getDynamicOpticSystem());
    editedVersionModel.setInfoTicketMachine(version1.getInfoTicketMachine());
    editedVersionModel.setAdditionalInformation(version1.getAdditionalInformation());
    editedVersionModel.setInteroperable(version1.getInteroperable());
    editedVersionModel.setUrl(version1.getUrl());
    editedVersionModel.setVisualInfo(version1.getVisualInfo());
    editedVersionModel.setWheelchairTicketMachine(version1.getWheelchairTicketMachine());
    editedVersionModel.setAssistanceRequestFulfilled(version1.getAssistanceRequestFulfilled());
    editedVersionModel.setTicketMachine(version1.getTicketMachine());
    editedVersionModel.setCreationDate(version1.getCreationDate());
    editedVersionModel.setEditionDate(version1.getEditionDate());
    editedVersionModel.setCreator(version1.getCreator());
    editedVersionModel.setEditor(version1.getEditor());
    editedVersionModel.setEtagVersion(version1.getVersion());
    editedVersionModel.setSloid("ch:1:sloid:12345");

    SharedServicePoint servicePoint = SharedServicePointTestData.buildSharedServicePoint("ch:1:sloid:12345", Set.of("ch:1:sboid"
        + ":100602"), Collections.emptySet());
    sharedServicePointRepository.saveAndFlush(servicePoint);

    //when && then
    mvc.perform(put("/v1/stop-points/" + version1.getId()).contentType(contentType)
            .content(mapper.writeValueAsString(editedVersionModel)))
        .andExpect(status().isOk());
    verify(prmChangeRecordingVariantService).stopPointChangeRecordingVariant(any(), any());
  }

  @Test
  void shouldGetStopPointVersionsBySloid() throws Exception {
    //given
    StopPointVersion version = stopPointRepository.save(StopPointTestData.getStopPointVersion());
    //when & then
    mvc.perform(get("/v1/stop-points/" + version.getSloid()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].sloid", is(version.getSloid())));
  }

  @Test
  void shouldReturnBadRequestWhenPageSizeExceeded() throws Exception {
    mvc.perform(get("/v1/stop-points?size=5000"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("The page size is limited to 2000")));
  }
}
