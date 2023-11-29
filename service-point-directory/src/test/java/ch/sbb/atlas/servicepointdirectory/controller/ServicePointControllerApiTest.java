package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel;
import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel.Fields;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointImportRequestModel;
import ch.sbb.atlas.journey.poi.model.CountryCode;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.config.JourneyPoiConfig;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointGeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointFotCommentRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.georeference.JourneyPoiClient;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointNumberService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MvcResult;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference.LV95;
import static ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference.WGS84;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServicePointControllerApiTest extends BaseControllerApiTest {

  @MockBean
  private JourneyPoiConfig journeyPoiConfig;
  @MockBean
  private JourneyPoiClient journeyPoiClient;
  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;
  @MockBean
  private ServicePointNumberService servicePointNumberService;

  private final ServicePointVersionRepository repository;
  private final ServicePointFotCommentRepository fotCommentRepository;
  private final ServicePointController servicePointController;
  private ServicePointVersion servicePointVersion;

  @Autowired
  ServicePointControllerApiTest(ServicePointVersionRepository repository,
      ServicePointFotCommentRepository fotCommentRepository, ServicePointController servicePointController) {
    this.repository = repository;
    this.fotCommentRepository = fotCommentRepository;
    this.servicePointController = servicePointController;
  }

  @BeforeEach
  void createDefaultVersion() {
    when(servicePointNumberService.getNextAvailableServicePointId(any())).thenReturn(1);
    servicePointVersion = repository.save(ServicePointTestData.getBernWyleregg());

    ResponseEntity<ch.sbb.atlas.journey.poi.model.Country> poiResponse =
        ResponseEntity.ofNullable(
            new ch.sbb.atlas.journey.poi.model.Country().countryCode(new CountryCode().isoCountryCode("RO")));
    when(journeyPoiClient.closestCountry(any(), any())).thenReturn(poiResponse);
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
    fotCommentRepository.deleteAll();
  }

  @Test
  void shouldGetServicePoint() throws Exception {
    mvc.perform(get("/v1/service-points/8589008")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue())))
        .andExpect(jsonPath("$[0].number.number", is(8589008)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.designationOfficial, is("Bern, Wyleregg")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.operatingPointRouteNetwork, is(true)))

        // IS_BETRIEBSPUNKT
        .andExpect(jsonPath("$[0].operatingPoint", is(true)))
        // IS_FAHRPLAN
        .andExpect(jsonPath("$[0].operatingPointWithTimetable", is(true)))
        // IS_HALTESTELLE
        .andExpect(jsonPath("$[0].stopPoint", is(true)))
        // IS_BEDIENPUNKT
        .andExpect(jsonPath("$[0].freightServicePoint", is(false)))
        // IS_VERKEHRSPUNKT
        .andExpect(jsonPath("$[0].trafficPoint", is(true)))
        // IS_GRENZPUNKT
        .andExpect(jsonPath("$[0].borderPoint", is(false)))
        // IS_VIRTUELL
        .andExpect(jsonPath("$[0].hasGeolocation", is(true)))

        .andExpect(jsonPath("$[0].operatingPointKilometer", is(false)))

        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$[0].creationDate", is("2021-03-22T09:26:29")))
        .andExpect(jsonPath("$[0].creator", is("fs45117")));
  }

  @Test
  void shouldGetServicePointVersions() throws Exception {
    mvc.perform(get("/v1/service-points")).andExpect(status().isOk())
        .andExpect(jsonPath("$.objects[0]." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue())))
        .andExpect(jsonPath("$.totalCount", is(1)));
  }

  @Test
  void shouldGetServicePointVersionById() throws Exception {
    mvc.perform(get("/v1/service-points/versions/" + servicePointVersion.getId())).andExpect(status().isOk());
  }

  @Test
  void shouldSearchServicePointSuccessfully() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("bern");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].number", is(8589008)))
        .andExpect(jsonPath("$[0].designationOfficial", is("Bern, Wyleregg")));
  }

  @Test
  void whenSearchRequestForSearchSePoWithNetworkTrueValidThenShouldFindServicePointSuccessfully() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("bern");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search-sp-with-route-network")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].number", is(8589008)))
        .andExpect(jsonPath("$[0].designationOfficial", is("Bern, Wyleregg")));
  }

  @Test
  void shouldSearchSwissOnlyServicePointSuccessfully() throws Exception {
    // given
    repository.save(ServicePointTestData.createAbroadServicePointVersion());

    ServicePointSearchRequest request = new ServicePointSearchRequest("bern");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search-swiss-only")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].number", is(8589008)))
        .andExpect(jsonPath("$[0].designationOfficial", is("Bern, Wyleregg")));
  }

  @Test
  void shouldReturnEmptyListWhenNoMatchFound() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("zug");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void whenSearchRequestForSearchSePoWithNetworkTrueValidThenShouldReturnEmptyList() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("zug");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search-sp-with-route-network")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
  }

  @Test
  void shouldReturnBadRequestWhenSearchWhitLessThanTwoDigit() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("b");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.details.[0].message", endsWith("You must enter at least 2 digits to start a search!")));
  }

  @Test
  void whenSearchRequestForSearchSePoWithNetworkTrueWithLessThanTwoDigitsThenShouldReturnBadRequest() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest("b");
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search-sp-with-route-network")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.details.[0].message", endsWith("You must enter at least 2 digits to start a search!")));
  }

  @Test
  void whenSearchRequestForSearchSePoWithNetworkTrueNullThenShouldReturnBadRequest() throws Exception {
    // given
    ServicePointSearchRequest request = new ServicePointSearchRequest(null);
    String jsonString = mapper.writeValueAsString(request);

    // when
    mvc.perform(post("/v1/service-points/search-sp-with-route-network")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.details.[0].message", endsWith("You must enter at least 2 digits to start a search!")));
  }

  @Test
  void shouldFindServicePointVersionByModifiedAfter() throws Exception {
    String modifiedAfterQueryString = servicePointVersion.getEditionDate().plusDays(1)
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
    mvc.perform(get("/v1/service-points?modifiedAfter=" + modifiedAfterQueryString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(0)));

    modifiedAfterQueryString = servicePointVersion.getEditionDate().minusDays(1)
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN));
    mvc.perform(get("/v1/service-points?modifiedAfter=" + modifiedAfterQueryString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)));
  }

  @Test
  void shouldFindServicePointVersionBycreatedAfterByISODateTime() throws Exception {
    ZonedDateTime zonedDateTime = servicePointVersion.getCreationDate().plusDays(1).atZone(ZoneId.of("Europe/Berlin"));
    String createdAfterQueryString = zonedDateTime.format(
        DateTimeFormatter.ofPattern(AtlasApiConstants.ISO_DATE_TIME_FORMAT_PATTERN));

    mvc.perform(get("/v1/service-points?createdAfter=" + createdAfterQueryString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(0)));

    createdAfterQueryString = servicePointVersion.getCreationDate().minusDays(1)
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.ISO_DATE_TIME_FORMAT_PATTERN));
    mvc.perform(get("/v1/service-points?createdAfter=" + createdAfterQueryString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)));
  }

  @Test
  void shouldFindServicePointVersionBycreatedAfterByDateTimeWithT() throws Exception {
    String createdAfterQueryString = servicePointVersion.getCreationDate().plusDays(1)
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN_WITH_T));

    mvc.perform(get("/v1/service-points?createdAfter=" + createdAfterQueryString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(0)));

    createdAfterQueryString = servicePointVersion.getCreationDate().minusDays(1)
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FORMAT_PATTERN_WITH_T));
    mvc.perform(get("/v1/service-points?createdAfter=" + createdAfterQueryString))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)));
  }

  @Test
  void shouldFindServicePointVersionByFromAndToDate() throws Exception {
    String fromDate = servicePointVersion.getValidFrom().minusDays(1)
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
    String toDate = servicePointVersion.getValidTo().plusDays(1)
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_FORMAT_PATTERN));
    mvc.perform(get("/v1/service-points?fromDate=" + fromDate + "&toDate=" + toDate))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalCount", is(1)));
  }

  @Test
  void shouldFailOnInvalidServicePointNumber() throws Exception {
    mvc.perform(get("/v1/service-points/1234567"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldFailOnFindWithInvalidServicePointNumber() throws Exception {
    mvc.perform(get("/v1/service-points?numbers=12345678"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.details[0].message",
            is("Value 12345678 rejected due to must be less than or equal to 9999999")));
  }

  @Test
  void shouldImportServicePointsSuccessfully() throws Exception {
    try (InputStream csvStream = this.getClass().getResourceAsStream("/SERVICE_POINTS_VERSIONING.csv")) {
      // given
      List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);
      List<ServicePointCsvModel> servicePointCsvModelsOrderedByValidFrom = servicePointCsvModels.stream()
          .sorted(Comparator.comparing(BaseDidokCsvModel::getValidFrom))
          .toList();
      int didokCode = servicePointCsvModels.get(0).getDidokCode();
      ServicePointImportRequestModel importRequestModel = new ServicePointImportRequestModel(
          List.of(
              ServicePointCsvModelContainer
                  .builder()
                  .servicePointCsvModelList(servicePointCsvModelsOrderedByValidFrom)
                  .didokCode(didokCode)
                  .build()
          )
      );
      String jsonString = mapper.writeValueAsString(importRequestModel);

      // when
      mvc.perform(post("/v1/service-points/import")
              .content(jsonString)
              .contentType(contentType))
          // then
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(5)));
    }
  }

  @Test
  void shouldReturnBadRequestOnEmptyListRequest() throws Exception {
    // given
    ServicePointImportRequestModel importRequestModel = new ServicePointImportRequestModel(
        Collections.emptyList()
    );
    String jsonString = mapper.writeValueAsString(importRequestModel);

    // when
    mvc.perform(post("/v1/service-points/import")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")));
  }

  @Test
  void shouldReturnBadRequestOnNullListRequest() throws Exception {
    // given
    ServicePointImportRequestModel importRequestModel = new ServicePointImportRequestModel();
    String jsonString = mapper.writeValueAsString(importRequestModel);

    // when
    mvc.perform(post("/v1/service-points/import")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")));
  }

  @Test
  void shouldReturnBadRequestOnNullImportRequestModel() throws Exception {
    // given & when
    mvc.perform(post("/v1/service-points/import")
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldSetStatusToRevokeForAllServicePoints() throws Exception {
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    UpdateServicePointVersionModel createServicePointVersionModel1 = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel1.setMeansOfTransport(List.of(MeanOfTransport.BUS));
    createServicePointVersionModel1.setValidFrom(LocalDate.of(2019, 8, 11));
    createServicePointVersionModel1.setValidTo(LocalDate.of(2020, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
            aargauServicePointVersionModel);
    Long id = servicePointVersionModel.getId();
    List<ReadServicePointVersionModel> servicePointVersionModels = servicePointController.updateServicePoint(id,
            createServicePointVersionModel1);
    servicePointVersionModels.forEach(v -> v.setStatus(Status.IN_REVIEW));
    Integer number = servicePointVersionModel.getNumber().getNumber();

    mvc.perform(post("/v1/service-points/" + number + "/revoke"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status", is(Status.REVOKED.toString())))
            .andExpect(jsonPath("$[1].status", is(Status.REVOKED.toString())));
  }

  @Test
  void shouldVerifyDesignationOfficialDesignationLongCanBeReusedAfterStatusRevoked() throws Exception {
    repository.deleteAll();
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setCountry(Country.GERMANY);
    aargauServicePointVersionModel.setNumberShort(12345);
    CreateServicePointVersionModel createServicePointVersionModel1 = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel1.setCountry(Country.GERMANY);
    createServicePointVersionModel1.setNumberShort(12345);
    createServicePointVersionModel1.setMeansOfTransport(List.of(MeanOfTransport.BUS));
    createServicePointVersionModel1.setValidFrom(LocalDate.of(2019, 8, 11));
    createServicePointVersionModel1.setValidTo(LocalDate.of(2020, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
            aargauServicePointVersionModel);
    Long id = servicePointVersionModel.getId();
    List<ReadServicePointVersionModel> servicePointVersionModels = servicePointController.updateServicePoint(id,
            createServicePointVersionModel1);
    servicePointVersionModels.forEach(v -> v.setStatus(Status.IN_REVIEW));
    Integer number = servicePointVersionModel.getNumber().getNumber();

    mvc.perform(post("/v1/service-points/" + number + "/revoke"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status", is(Status.REVOKED.toString())))
            .andExpect(jsonPath("$[1].status", is(Status.REVOKED.toString())));

    CreateServicePointVersionModel buchsiServicePoint = ServicePointTestData.getBuchsiServicePoint();
    buchsiServicePoint.setCountry(Country.GERMANY);
    buchsiServicePoint.setNumberShort(55555);
    buchsiServicePoint.setValidFrom(LocalDate.of(2019, 8, 11));
    buchsiServicePoint.setValidTo(LocalDate.of(2020, 8, 10));
    buchsiServicePoint.setDesignationLong("designation long 1");
    buchsiServicePoint.setDesignationOfficial("Aargau Strasse");
    buchsiServicePoint.setAbbreviation("NEWABC");
    buchsiServicePoint.setBusinessOrganisation("ch:1:sboid:100879");
    mvc.perform(post("/v1/service-points")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(buchsiServicePoint)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.designationOfficial", is("Aargau Strasse")))
            .andExpect(jsonPath("$.designationLong", is("designation long 1")))
            .andExpect(jsonPath("$.abbreviation", is("NEWABC")));
    mvc.perform(get("/v1/service-points"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalCount", is(3)))
            .andExpect(jsonPath("$.objects[0].abbreviation", is("ABC")))
            .andExpect(jsonPath("$.objects[0].designationLong", is("designation long 1")))
            .andExpect(jsonPath("$.objects[0].designationOfficial", is("Aargau Strasse")))
            .andExpect(jsonPath("$.objects[0].number.number", is(8012345)))
            .andExpect(jsonPath("$.objects[1].abbreviation", is("ABC")))
            .andExpect(jsonPath("$.objects[1].designationLong", is("designation long 1")))
            .andExpect(jsonPath("$.objects[1].designationOfficial", is("Aargau Strasse")))
            .andExpect(jsonPath("$.objects[1].number.number", is(8012345)))
            .andExpect(jsonPath("$.objects[2].abbreviation", is("NEWABC")))
            .andExpect(jsonPath("$.objects[2].designationLong", is("designation long 1")))
            .andExpect(jsonPath("$.objects[2].designationOfficial", is("Aargau Strasse")))
            .andExpect(jsonPath("$.objects[2].number.number", is(8055555)));
  }

  @Test
  void shouldNotAllowReuseDesignationOfficialAndDesignationLongOnTwoServicePoints() throws Exception {
    repository.deleteAll();
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setCountry(Country.GERMANY);
    aargauServicePointVersionModel.setNumberShort(12345);
    CreateServicePointVersionModel createServicePointVersionModel1 = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel1.setCountry(Country.GERMANY);
    createServicePointVersionModel1.setNumberShort(12345);
    createServicePointVersionModel1.setMeansOfTransport(List.of(MeanOfTransport.BUS));
    createServicePointVersionModel1.setValidFrom(LocalDate.of(2019, 8, 11));
    createServicePointVersionModel1.setValidTo(LocalDate.of(2020, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
            aargauServicePointVersionModel);
    Long id = servicePointVersionModel.getId();
    List<ReadServicePointVersionModel> servicePointVersionModels = servicePointController.updateServicePoint(id,
            createServicePointVersionModel1);
    servicePointVersionModels.forEach(v -> v.setStatus(Status.IN_REVIEW));
    Integer number = servicePointVersionModel.getNumber().getNumber();

    CreateServicePointVersionModel buchsiServicePoint = ServicePointTestData.getBuchsiServicePoint();
    buchsiServicePoint.setCountry(Country.GERMANY);
    buchsiServicePoint.setNumberShort(55555);
    buchsiServicePoint.setValidFrom(LocalDate.of(2019, 8, 11));
    buchsiServicePoint.setValidTo(LocalDate.of(2020, 8, 10));
    buchsiServicePoint.setDesignationLong("designation long 1");
    buchsiServicePoint.setDesignationOfficial("Aargau Strasse");
    buchsiServicePoint.setAbbreviation("NEWABC");
    buchsiServicePoint.setBusinessOrganisation("ch:1:sboid:100879");
    mvc.perform(post("/v1/service-points")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(buchsiServicePoint)))
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message", is("A conflict occurred due to a business rule while saving 8055555")))
            .andExpect(jsonPath("$.details.[0].message", endsWith(
                    "DesignationOfficial Aargau Strasse already taken from 11.08.2019 to 10.08.2020 by 8012345")));
  }

  @Test
  void shouldNotAllowAbbreviationReuseAfterStatusRevoked() throws Exception {
    repository.deleteAll();
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setCountry(Country.GERMANY);
    aargauServicePointVersionModel.setNumberShort(12345);
    CreateServicePointVersionModel createServicePointVersionModel1 = ServicePointTestData.getAargauServicePointVersionModel();
    createServicePointVersionModel1.setCountry(Country.GERMANY);
    createServicePointVersionModel1.setNumberShort(12345);
    createServicePointVersionModel1.setMeansOfTransport(List.of(MeanOfTransport.BUS));
    createServicePointVersionModel1.setValidFrom(LocalDate.of(2019, 8, 11));
    createServicePointVersionModel1.setValidTo(LocalDate.of(2020, 8, 10));
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
            aargauServicePointVersionModel);
    Long id = servicePointVersionModel.getId();
    List<ReadServicePointVersionModel> servicePointVersionModels = servicePointController.updateServicePoint(id,
            createServicePointVersionModel1);
    servicePointVersionModels.forEach(v -> v.setStatus(Status.IN_REVIEW));
    Integer number = servicePointVersionModel.getNumber().getNumber();

    mvc.perform(post("/v1/service-points/" + number + "/revoke"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].status", is(Status.REVOKED.toString())))
            .andExpect(jsonPath("$[1].status", is(Status.REVOKED.toString())));

    CreateServicePointVersionModel buchsiServicePoint = ServicePointTestData.getBuchsiServicePoint();
    buchsiServicePoint.setCountry(Country.GERMANY);
    buchsiServicePoint.setNumberShort(55555);
    buchsiServicePoint.setValidFrom(LocalDate.of(2019, 8, 11));
    buchsiServicePoint.setValidTo(LocalDate.of(2020, 8, 10));
    buchsiServicePoint.setDesignationLong("designation long 1");
    buchsiServicePoint.setDesignationOfficial("Aargau Strasse");
    buchsiServicePoint.setAbbreviation("ABC");
    buchsiServicePoint.setBusinessOrganisation("ch:1:sboid:100879");
    mvc.perform(post("/v1/service-points")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(buchsiServicePoint)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("The abbreviation must be unique and the chosen servicepoint version should be the most recent version.")))
            .andExpect(jsonPath("$.details.[0].message", endsWith(
                    "The abbreviation must be unique and the chosen servicepoint version should be the most recent version.")));
  }

  @Test
  void shouldThrowExceptionOnRevoke() throws Exception {
    Integer number = 1234567;

    mvc.perform(post("/v1/service-points/" + number + "/revoke"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message", is("Entity not found")))
            .andExpect(jsonPath("$.details.[0].message", endsWith(
                    "Object with servicePointNumber 1234567 not found")));;
  }

  @Test
  void shouldSetStatusToValidateForServicePoint() throws Exception {
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
            aargauServicePointVersionModel);
    Long id = servicePointVersionModel.getId();

    mvc.perform(post("/v1/service-points/versions/" + id + "/skip-workflow"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is(Status.VALIDATED.toString())));
  }

  @Test
  void shouldCreateServicePoint() throws Exception {
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(ServicePointTestData.getAargauServicePointVersionModel())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
        .andExpect(jsonPath("$.number.number", is(8500001)))
        .andExpect(jsonPath("$.number.numberShort", is(1)))
        .andExpect(jsonPath("$.number.checkDigit", is(8)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationOfficial, is("Aargau Strasse")))
        .andExpect(jsonPath("$." + ReadServicePointVersionModel.Fields.sloid, is("ch:1:sloid:1")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationLong, is("designation long 1")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.abbreviation, is("ABC")))
        .andExpect(jsonPath("$.operatingPoint", is(true)))
        .andExpect(jsonPath("$.operatingPointWithTimetable", is(true)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.freightServicePoint, is(false)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.sortCodeOfDestinationStation, is("39136")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.businessOrganisation, is("ch:1:sboid:100871")))
        .andExpect(jsonPath("$.categories[0]", is("POINT_OF_SALE")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointRouteNetwork, is(true)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.number", is(8500001)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.numberShort", is(1)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.checkDigit", is(8)))
        .andExpect(jsonPath("$.meansOfTransport[0]", is("TRAIN")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.stopPointType, is("ON_REQUEST")))
        .andExpect(jsonPath("$.servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.north", is(1201099.0)))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.east", is(2600783.0)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.north", is(46.96096808019)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.east", is(7.44891972221)))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.canton", is("BERN")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.district.districtName", is("Bern-Mittelland")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.localityMunicipality.municipalityName", is("Bern")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.localityMunicipality.localityName", is("Bern")))
        .andExpect(jsonPath("$." + ReadServicePointVersionModel.Fields.status, is("VALIDATED")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$.operatingPointKilometer", is(true)))
        .andExpect(jsonPath("$.stopPoint", is(true)))
        .andExpect(jsonPath("$.fareStop", is(false)))
        .andExpect(jsonPath("$.borderPoint", is(false)))
        .andExpect(jsonPath("$.trafficPoint", is(true)))
        .andExpect(jsonPath("$.hasGeolocation", is(true)))
        .andExpect(jsonPath("$.creator", is("e123456")));
  }

  @Test
  void shouldThrowExceptionWhenOperatingPointRouteNetworkTrueAndOperatingPointKilometerMasterNotNull() throws Exception {
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setOperatingPointKilometerMasterNumber(8034511);
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.details.[0].message", endsWith(
            "If OperatingPointRouteNetwork is true, then operatingPointKilometerMaster will be set to the same value as "
                + "numberWithoutCheckDigit and it should not be sent in the request")));
  }

  @Test
  void shouldCreateServicePointWhenOperatingPointRouteNetworkTrueAndOperatingPointKilometerMasterNull() throws Exception {
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setOperatingPointKilometerMasterNumber(null);
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
        .andExpect(jsonPath("$.number.number", is(8500001)))
        .andExpect(jsonPath("$.number.numberShort", is(1)))
        .andExpect(jsonPath("$.number.checkDigit", is(8)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointRouteNetwork, is(true)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.number", is(8500001)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.numberShort", is(1)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.checkDigit", is(8)));
  }

  @Test
  void shouldCreateServicePointWhenOperatingPointRouteNetworkFalseAndOperatingPointKilometerMasterNotNull() throws Exception {
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setOperatingPointRouteNetwork(false);
    aargauServicePointVersionModel.setOperatingPointKilometerMasterNumber(8589008);
    aargauServicePointVersionModel.setValidFrom(LocalDate.of(2014, 12, 14));
    aargauServicePointVersionModel.setValidTo(LocalDate.of(2021, 3, 31));
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
        .andExpect(jsonPath("$.number.number", is(8500001)))
        .andExpect(jsonPath("$.number.numberShort", is(1)))
        .andExpect(jsonPath("$.number.checkDigit", is(8)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointRouteNetwork, is(false)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.number", is(8589008)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.numberShort", is(89008)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.checkDigit", is(7)));
  }

  @Test
  void shouldCreateServicePointWhenOperatingPointRouteNetworkFalseAndOperatingPointKilometerMasterNull() throws Exception {
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setOperatingPointRouteNetwork(false);
    aargauServicePointVersionModel.setOperatingPointKilometerMasterNumber(null);
    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
        .andExpect(jsonPath("$.number.number", is(8500001)))
        .andExpect(jsonPath("$.number.numberShort", is(1)))
        .andExpect(jsonPath("$.number.checkDigit", is(8)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointRouteNetwork, is(false)));
  }

  @Test
  void shouldThrowExceptionWhenCreateServicePointWithRouteNetworkTrueAndNotStopOrControlOrOperatingPoint() throws Exception {
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setOperatingPointRouteNetwork(true);
    aargauServicePointVersionModel.setMeansOfTransport(new ArrayList<>());
    aargauServicePointVersionModel.setStopPointType(null);
    aargauServicePointVersionModel.setFreightServicePoint(false);
    aargauServicePointVersionModel.setOperatingPointType(null);
    aargauServicePointVersionModel.setOperatingPointTechnicalTimetableType(null);
    aargauServicePointVersionModel.setOperatingPointTrafficPointType(null);
    mvc.perform(post("/v1/service-points")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
            .andExpect(jsonPath("$.details.[0].message", endsWith(
                    "OperatingPointRouteNetwork true is allowed only for StopPoint, ControlPoint and OperatingPoint." +
                    " OperatingPointKilometerMasterNumber can be set only for StopPoint, ControlPoint and OperatingPoint.")));
  }

  @Test
  void shouldCreateServicePointWithRouteNetworkTrue() throws Exception {
    CreateServicePointVersionModel servicePointWithOperationPointRouteNetworkTrue =
        ServicePointTestData.getAargauServicePointVersionModel();
    servicePointWithOperationPointRouteNetworkTrue.setOperatingPointRouteNetwork(true);

    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(servicePointWithOperationPointRouteNetworkTrue)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
        .andExpect(jsonPath("$.number.number", is(8500001)))
        .andExpect(jsonPath("$.number.numberShort", is(1)))
        .andExpect(jsonPath("$.number.checkDigit", is(8)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationOfficial, is("Aargau Strasse")))
        .andExpect(jsonPath("$." + ReadServicePointVersionModel.Fields.sloid, is("ch:1:sloid:1")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationLong, is("designation long 1")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.abbreviation, is("ABC")))
        .andExpect(jsonPath("$.operatingPoint", is(true)))
        .andExpect(jsonPath("$.operatingPointWithTimetable", is(true)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.freightServicePoint, is(false)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.sortCodeOfDestinationStation, is("39136")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.businessOrganisation, is("ch:1:sboid:100871")))
        .andExpect(jsonPath("$.categories[0]", is("POINT_OF_SALE")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointRouteNetwork, is(true)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.number", is(8500001)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.numberShort", is(1)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.checkDigit", is(8)))
        .andExpect(jsonPath("$.meansOfTransport[0]", is("TRAIN")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.stopPointType, is("ON_REQUEST")))
        .andExpect(jsonPath("$.servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.north", is(1201099.0)))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.east", is(2600783.0)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.north", is(46.96096808019)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.east", is(7.44891972221)))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.canton", is("BERN")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.district.districtName", is("Bern-Mittelland")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.localityMunicipality.municipalityName", is("Bern")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.localityMunicipality.localityName", is("Bern")))
        .andExpect(jsonPath("$." + ReadServicePointVersionModel.Fields.status, is("VALIDATED")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$.operatingPointKilometer", is(true)))
        .andExpect(jsonPath("$.stopPoint", is(true)))
        .andExpect(jsonPath("$.fareStop", is(false)))
        .andExpect(jsonPath("$.borderPoint", is(false)))
        .andExpect(jsonPath("$.trafficPoint", is(true)))
        .andExpect(jsonPath("$.hasGeolocation", is(true)))
        .andExpect(jsonPath("$.creator", is("e123456")));
  }

  @Test
  void shouldUpdateServicePointAndCreateMultipleVersions() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        ServicePointTestData.getAargauServicePointVersionModel());
    Long id = servicePointVersionModel.getId();
    Integer numberShort = servicePointVersionModel.getNumber().getNumberShort();

    UpdateServicePointVersionModel newServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    newServicePointVersionModel.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
    newServicePointVersionModel.setValidFrom(LocalDate.of(2011, 12, 11));
    newServicePointVersionModel.setValidTo(LocalDate.of(2012, 12, 11));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(newServicePointVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validFrom, is("2010-12-11")))
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.validTo, is("2011-12-10")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$[0].servicePointGeolocation.lv95.north", is(1201099.0)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.lv95.east", is(2600783.0)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.wgs84.north", is(46.96096808019)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.wgs84.east", is(7.44891972221)))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2011-12-11")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2012-12-11")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$[1].servicePointGeolocation.lv95.north", is(1200000D)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.lv95.east", is(2600000D)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.wgs84.north", is(46.95108277187)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.wgs84.east", is(7.43863242087)))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2012-12-12")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$[2].servicePointGeolocation.lv95.north", is(1201099.0)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.lv95.east", is(2600783.0)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.wgs84.north", is(46.96096808019)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.wgs84.east", is(7.44891972221)));
  }

  @Test
  void shouldThrowExceptionWhenUpdateServicePointWithRouteNetworkTrueAndNotStopOrControlOrOperatingPoint() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
            ServicePointTestData.getAargauServicePointVersionModelWithRouteNetworkFalse());
    Long id = servicePointVersionModel.getId();

    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setOperatingPointRouteNetwork(true);
    aargauServicePointVersionModel.setMeansOfTransport(new ArrayList<>());
    aargauServicePointVersionModel.setStopPointType(null);
    aargauServicePointVersionModel.setFreightServicePoint(false);
    aargauServicePointVersionModel.setOperatingPointType(null);
    aargauServicePointVersionModel.setOperatingPointTechnicalTimetableType(null);
    aargauServicePointVersionModel.setOperatingPointTrafficPointType(null);
    mvc.perform(put("/v1/service-points/" + id)
                  .contentType(contentType)
                  .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
          .andExpect(jsonPath("$.details.[0].message", endsWith(
                  "OperatingPointRouteNetwork true is allowed only for StopPoint, ControlPoint and OperatingPoint." +
                  " OperatingPointKilometerMasterNumber can be set only for StopPoint, ControlPoint and OperatingPoint.")));
  }

  @Test
  void shouldUpdateServicePointAndNotCreateMultipleVersions() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        ServicePointTestData.getAargauServicePointVersionModelWithRouteNetworkFalse());
    Long id = servicePointVersionModel.getId();
    Integer numberShort = servicePointVersionModel.getNumber().getNumberShort();

    CreateServicePointVersionModel newServicePointVersionModel =
        ServicePointTestData.getAargauServicePointVersionModelWithRouteNetworkFalse();
    newServicePointVersionModel.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
    newServicePointVersionModel.setNumberShort(numberShort);

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(newServicePointVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldUpdateServicePointWithRouteNetworkTrueAndNotCreateMultipleVersions() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        ServicePointTestData.getAargauServicePointVersionModel());
    Long id = servicePointVersionModel.getId();

    UpdateServicePointVersionModel newServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    newServicePointVersionModel.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
    newServicePointVersionModel.setOperatingPointRouteNetwork(true);

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(newServicePointVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].operatingPointRouteNetwork", is(true)))
        .andExpect(jsonPath("$[0].operatingPointKilometerMaster.number", is(8500001)))
        .andExpect(jsonPath("$[0].operatingPointKilometerMaster.numberShort", is(1)))
        .andExpect(jsonPath("$[0].operatingPointKilometerMaster.checkDigit", is(8)))
        .andExpect(jsonPath("$", hasSize(1)));
  }

  @Test
  void shouldThrowForbiddenDueToChosenServicePointVersionValidationPeriod() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        ServicePointTestData.getAargauServicePointVersionModel());
    Long id = servicePointVersionModel.getId();

    CreateServicePointVersionModel newServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    newServicePointVersionModel.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));
    newServicePointVersionModel.setOperatingPointRouteNetwork(false);
    newServicePointVersionModel.setOperatingPointKilometerMasterNumber(8034511);

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(newServicePointVersionModel)))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void shouldReadServicePointWithOperatingPointFalseCorrectly() throws Exception {
    ServicePointNumber number = ServicePointNumber.ofNumberWithoutCheckDigit(8590008);
    repository.save(ServicePointVersion
        .builder()
        .number(number)
        .sloid("ch:1:sloid:8590008")
        .numberShort(number.getNumberShort())
        .country(Country.SWITZERLAND)
        .designationLong(null)
        .designationOfficial("Bern, Fake thing")
        .abbreviation(null)
        .businessOrganisation("ch:1:sboid:100626")
        .status(Status.VALIDATED)
        .validFrom(LocalDate.of(2014, 12, 14))
        .validTo(LocalDate.of(2021, 3, 31))
        .operatingPoint(true)
        .operatingPointWithTimetable(true)
        .freightServicePoint(true)
        .creationDate(LocalDateTime.of(LocalDate.of(2021, 3, 22), LocalTime.of(9, 26, 29)))
        .creator("fs45117")
        .editionDate(LocalDateTime.of(LocalDate.of(2022, 2, 23), LocalTime.of(17, 10, 10)))
        .editor("fs45117")
        .build());

    mvc.perform(get("/v1/service-points/" + number.getValue()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].number.number", is(number.getNumber())))
        .andExpect(jsonPath("$[0].operatingPoint", is(true)))
        .andExpect(jsonPath("$[0].operatingPointWithTimetable", is(true)))
        .andExpect(jsonPath("$[0].freightServicePoint", is(true)))
        .andExpect(jsonPath("$[0].trafficPoint", is(true)));
  }

  @Test
  void shouldReturnOptimisticLockingErrorResponse() throws Exception {
    //given
    CreateServicePointVersionModel createServicePointVersionModel =
        ServicePointTestData.getAargauServicePointVersionModelWithRouteNetworkFalse();
    ReadServicePointVersionModel savedServicePoint = servicePointController.createServicePoint(createServicePointVersionModel);

    // When first update it is ok
    createServicePointVersionModel.setId(savedServicePoint.getId());
    createServicePointVersionModel.setNumberShort(savedServicePoint.getNumber().getNumberShort());
    createServicePointVersionModel.setEtagVersion(savedServicePoint.getEtagVersion());

    createServicePointVersionModel.setDesignationLong("New and hot service point, ready to roll");
    mvc.perform(put("/v1/service-points/" + createServicePointVersionModel.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(createServicePointVersionModel)))
        .andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    createServicePointVersionModel.setDesignationLong("New and hot line, ready to rock");
    MvcResult mvcResult = mvc.perform(put("/v1/service-points/" + createServicePointVersionModel.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(createServicePointVersionModel)))
        .andExpect(status().isPreconditionFailed()).andReturn();

    ErrorResponse errorResponse = mapper.readValue(
        mvcResult.getResponse().getContentAsString(), ErrorResponse.class);

    assertThat(errorResponse.getStatus()).isEqualTo(HttpStatus.PRECONDITION_FAILED.value());
    assertThat(errorResponse.getDetails()).size().isEqualTo(1);
    assertThat(errorResponse.getDetails().first().getDisplayInfo().getCode()).isEqualTo(
        "COMMON.NOTIFICATION.OPTIMISTIC_LOCK_ERROR");
    assertThat(errorResponse.getError()).isEqualTo("Stale object state error");
  }

  @Test
  void shouldCreateServicePointFotComment() throws Exception {
    ServicePointFotCommentModel fotComment = ServicePointFotCommentModel.builder()
        .fotComment("Very important on demand service point")
        .build();

    mvc.perform(put("/v1/service-points/" + servicePointVersion.getNumber().getValue() + "/fot-comment")
            .contentType(contentType)
            .content(mapper.writeValueAsString(fotComment)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.fotComment, is("Very important on demand service point")));
  }

  @Test
  void shouldGetServicePointFotComment() throws Exception {
    ServicePointFotCommentModel fotComment = ServicePointFotCommentModel.builder()
        .fotComment("Very important on demand service point")
        .build();

    servicePointController.saveFotComment(servicePointVersion.getNumber().getValue(), fotComment);

    mvc.perform(get("/v1/service-points/" + servicePointVersion.getNumber().getValue() + "/fot-comment"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.fotComment, is("Very important on demand service point")));
  }

  @Test
  void shouldCreateServicePointWithLv03ConvertingToLv95() throws Exception {
    UpdateServicePointVersionModel aargauServicePointVersion =
        ServicePointTestData.getAargauServicePointVersionModelWithRouteNetworkFalse();
    aargauServicePointVersion.getServicePointGeolocation().setSpatialReference(SpatialReference.LV03);
    aargauServicePointVersion.getServicePointGeolocation().setEast(600127.58303);
    aargauServicePointVersion.getServicePointGeolocation().setNorth(199776.88044);

    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(aargauServicePointVersion)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
        .andExpect(jsonPath("$.number.number", is(8500001)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationOfficial, is("Aargau Strasse")))
        .andExpect(jsonPath("$." + ReadServicePointVersionModel.Fields.sloid, is("ch:1:sloid:1")))
        .andExpect(jsonPath("$.servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.east", is(2600127.58303)))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.north", is(1199776.88044)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.north", is(46.94907577445)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.east", is(7.44030833981)))
        .andExpect(jsonPath("$.servicePointGeolocation.lv03.north", is(199776.88044)))
        .andExpect(jsonPath("$.servicePointGeolocation.lv03.east", is(600127.58303)))
        .andExpect(jsonPath("$.hasGeolocation", is(true)));
  }

  @Test
  void shouldCreateServicePointWithWgs84webConvertingToWgs84() throws Exception {
    UpdateServicePointVersionModel aargauServicePointVersion =
        ServicePointTestData.getAargauServicePointVersionModelWithRouteNetworkFalse();
    aargauServicePointVersion.getServicePointGeolocation().setSpatialReference(SpatialReference.WGS84WEB);
    aargauServicePointVersion.getServicePointGeolocation().setEast(828251.335735);
    aargauServicePointVersion.getServicePointGeolocation().setNorth(5933765.900287);

    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(aargauServicePointVersion)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
        .andExpect(jsonPath("$.number.number", is(8500001)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationOfficial, is("Aargau Strasse")))
        .andExpect(jsonPath("$." + ReadServicePointVersionModel.Fields.sloid, is("ch:1:sloid:1")))
        .andExpect(jsonPath("$.servicePointGeolocation.spatialReference", is(WGS84.toString())))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.north", is(46.94907577445)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.east", is(7.44030833983)))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.east", is(2600127.58359)))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.north", is(1199776.88159)))
        .andExpect(jsonPath("$.hasGeolocation", is(true)));
  }

  @Test
  void shouldCreateServicePointAndGenerateServicePointNumber() throws Exception {
    repository.deleteAll();
    CreateServicePointVersionModel servicePointVersionModel = CreateServicePointVersionModel.builder()
        .country(Country.SWITZERLAND)
        .designationOfficial("Bern")
        .businessOrganisation("ch:1:sboid:5846489645")
        .validFrom(LocalDate.of(2022, 1, 1))
        .validTo(LocalDate.of(2022, 12, 31))
        .build();

    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(servicePointVersionModel)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.number.number", is(8500001)))
        .andExpect(jsonPath("$.sloid", is("ch:1:sloid:1")));
  }

  @Test
  void shouldNotUpdateServicePointIfAbbreviationInvalid() throws Exception {
    CreateServicePointVersionModel testData = ServicePointTestData.getAargauServicePointVersionModel();
    testData.setAbbreviation(null);
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(testData);
    Long id = servicePointVersionModel.getId();

    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setId(id);

    aargauServicePointVersionModel.setAbbreviation("dasisteinevielzulangeabkuerzung");

    mvc.perform(put("/v1/service-points/" + aargauServicePointVersionModel.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldNotUpdateServicePointAbbreviationIfBusinessOrganisationNotAllowed() throws Exception {
    CreateServicePointVersionModel testData = ServicePointTestData.getAargauServicePointVersionModel();
    testData.setAbbreviation(null);

    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(testData);
    Long id = servicePointVersionModel.getId();

    CreateServicePointVersionModel aargauServicePoint = testData;
    aargauServicePoint.setBusinessOrganisation("dasisteineungueltigebusinessorganisation");
    aargauServicePoint.setId(id);

    aargauServicePoint.setAbbreviation("BUCH");

    mvc.perform(put("/v1/service-points/" + aargauServicePoint.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(aargauServicePoint)))
        .andExpect(status().isForbidden());
  }

  @Test
  void shouldNotUpdateServicePointAbbreviationIfNewAbbreviationNotEqualsOldAbbreviation() throws Exception {
    CreateServicePointVersionModel testData = ServicePointTestData.getAargauServicePointVersionModel();
    testData.setAbbreviation("BUCH");

    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(testData);

    Long id = servicePointVersionModel.getId();

    CreateServicePointVersionModel buchsiServicePoint = ServicePointTestData.getBuchsiServicePoint();
    buchsiServicePoint.setId(id);

    buchsiServicePoint.setAbbreviation("NEU");

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(buchsiServicePoint)))
        .andExpect(status().isForbidden());
  }

}
