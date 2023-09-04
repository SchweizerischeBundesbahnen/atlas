package ch.sbb.atlas.servicepointdirectory.controller;

import static ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference.LV95;
import static ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference.WGS84;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel;
import ch.sbb.atlas.api.servicepoint.ServicePointFotCommentModel.Fields;
import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.atlas.business.organisation.service.SharedBusinessOrganisationService;
import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointImportRequestModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.OperatingPointTrafficPointType;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.mapper.ServicePointGeolocationMapper;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointFotCommentRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

public class ServicePointControllerApiTest extends BaseControllerApiTest {

  @MockBean
  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  private final ServicePointVersionRepository repository;
  private final ServicePointFotCommentRepository fotCommentRepository;
  private final ServicePointController servicePointController;
  private ServicePointVersion servicePointVersion;

  @Autowired
  public ServicePointControllerApiTest(ServicePointVersionRepository repository,
      ServicePointFotCommentRepository fotCommentRepository, ServicePointController servicePointController) {
    this.repository = repository;
    this.fotCommentRepository = fotCommentRepository;
    this.servicePointController = servicePointController;
  }

  @BeforeEach
  void createDefaultVersion() {
    servicePointVersion = repository.save(ServicePointTestData.getBernWyleregg());
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
        .andExpect(jsonPath("$[0]." + ServicePointVersionModel.Fields.operatingPointRouteNetwork, is(false)))

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
    mvc.perform(get("/v1/service-points/123"))
        .andExpect(status().isNotFound());
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
  void shouldReturnBadRequestWhenCreateServicePointWithOperatingPointTrafficPointTypeAndOperatingPointTechnicalTimetableType() throws Exception {
    CreateServicePointVersionModel aargauServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersionModel.setOperatingPointTrafficPointType(OperatingPointTrafficPointType.TARIFF_POINT);
    mvc.perform(post("/v1/service-points")
                    .contentType(contentType)
                    .content(mapper.writeValueAsString(aargauServicePointVersionModel)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
            .andExpect(jsonPath("$.details[0].message", is("Value false rejected due to At most one of OperatingPointTechnicalTimetableType, OperatingPointTrafficPointType may be set")));
  }

  @Test
  void shouldCreateServicePoint() throws Exception {

    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(ServicePointTestData.getAargauServicePointVersionModel())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
        .andExpect(jsonPath("$.number.number", is(8034510)))
        .andExpect(jsonPath("$.number.numberShort", is(34510)))
        .andExpect(jsonPath("$.number.checkDigit", is(8)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationOfficial, is("Aargau Strasse")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.sloid, is("ch:1:sloid:18771")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationLong, is("designation long 1")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.abbreviation, is("3")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPoint, is(true)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointWithTimetable, is(true)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.freightServicePoint, is(false)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.sortCodeOfDestinationStation, is("39136")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.businessOrganisation, is("ch:1:sboid:100871")))
        .andExpect(jsonPath("$.categories[0]", is("POINT_OF_SALE")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointType, is("INVENTORY_POINT")))
        .andExpect(
            jsonPath("$." + ServicePointVersionModel.Fields.operatingPointTechnicalTimetableType, is("ASSIGNED_OPERATING_POINT")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.operatingPointRouteNetwork, is(false)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.number", is(8034511)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.numberShort", is(34511)))
        .andExpect(jsonPath("$.operatingPointKilometerMaster.checkDigit", is(6)))
        .andExpect(jsonPath("$.meansOfTransport[0]", is("TRAIN")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.stopPointType, is("ON_REQUEST")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$.servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.north", is(1201099.0)))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.east", is(2600783.0)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.north", is(46.96096808019)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.east", is(7.44891972221)))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.canton", is("BERN")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.cantonInformation.name", is("Bern")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.cantonInformation.abbreviation", is("BE")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.district.fsoNumber", is(246)))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.district.districtName", is("Bern-Mittelland")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.localityMunicipality.municipalityName", is("Bern")))
        .andExpect(jsonPath("$.servicePointGeolocation.swissLocation.localityMunicipality.localityName", is("Bern")))

        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.status, is("VALIDATED")))
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
  public void shouldUpdateServicePointAndCreateMultipleVersions() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        ServicePointTestData.getAargauServicePointVersionModel());
    Long id = servicePointVersionModel.getId();

    CreateServicePointVersionModel newServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
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
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$[0].servicePointGeolocation.lv95.north", is(1201099.0)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.lv95.east", is(2600783.0)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.wgs84.north", is(46.96096808019)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.wgs84.east", is(7.44891972221)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.canton", is("BERN")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.cantonInformation.name", is("Bern")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.cantonInformation.abbreviation", is("BE")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.district.fsoNumber", is(246)))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.district.districtName", is("Bern-Mittelland")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.localityMunicipality.municipalityName", is("Bern")))
        .andExpect(jsonPath("$[0].servicePointGeolocation.swissLocation.localityMunicipality.localityName", is("Bern")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validFrom, is("2011-12-11")))
        .andExpect(jsonPath("$[1]." + ServicePointVersionModel.Fields.validTo, is("2012-12-11")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(19)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$[1].servicePointGeolocation.lv95.north", is(1485245.92913)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.lv95.east", is(2671984.26107)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.wgs84.north", is(49.51139999799)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.wgs84.east", is(8.43160000001)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.canton", is("AARGAU")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(19)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.cantonInformation.name", is("Aargau")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.cantonInformation.abbreviation", is("AG")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.district.fsoNumber", is(1909)))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.district.districtName", is("Rheinfelden")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.localityMunicipality.municipalityName", is("Hellikon")))
        .andExpect(jsonPath("$[1].servicePointGeolocation.swissLocation.localityMunicipality.localityName", is("Hellikon")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validFrom, is("2012-12-12")))
        .andExpect(jsonPath("$[2]." + ServicePointVersionModel.Fields.validTo, is("2019-08-10")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.spatialReference", is(LV95.toString())))
        .andExpect(jsonPath("$[2].servicePointGeolocation.lv95.north", is(1201099.0)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.lv95.east", is(2600783.0)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.wgs84.north", is(46.96096808019)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.wgs84.east", is(7.44891972221)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.canton", is("BERN")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.cantonInformation.fsoNumber", is(2)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.cantonInformation.name", is("Bern")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.cantonInformation.abbreviation", is("BE")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.district.fsoNumber", is(246)))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.district.districtName", is("Bern-Mittelland")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.localityMunicipality.municipalityName", is("Bern")))
        .andExpect(jsonPath("$[2].servicePointGeolocation.swissLocation.localityMunicipality.localityName", is("Bern")));
  }

  @Test
  public void shouldUpdateServicePointAndNotCreateMultipleVersions() throws Exception {
    ReadServicePointVersionModel servicePointVersionModel = servicePointController.createServicePoint(
        ServicePointTestData.getAargauServicePointVersionModel());
    Long id = servicePointVersionModel.getId();

    CreateServicePointVersionModel newServicePointVersionModel = ServicePointTestData.getAargauServicePointVersionModel();
    newServicePointVersionModel.setServicePointGeolocation(
        ServicePointGeolocationMapper.toCreateModel(ServicePointTestData.getAargauServicePointGeolocation()));

    mvc.perform(put("/v1/service-points/" + id)
            .contentType(contentType)
            .content(mapper.writeValueAsString(newServicePointVersionModel)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
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
        ServicePointTestData.getAargauServicePointVersionModel();
    ReadServicePointVersionModel savedServicePoint = servicePointController.createServicePoint(createServicePointVersionModel);

    // When first update it is ok
    createServicePointVersionModel.setId(savedServicePoint.getId());
    createServicePointVersionModel.setNumberWithoutCheckDigit(savedServicePoint.getNumber().getNumber());
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

    mvc.perform(put("/v1/service-points/"+servicePointVersion.getNumber().getValue()+"/fot-comment")
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

    mvc.perform(get("/v1/service-points/"+servicePointVersion.getNumber().getValue()+"/fot-comment"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$." + Fields.fotComment, is("Very important on demand service point")));
  }

  @Test
  void shouldCreateServicePointWithLv03ConvertingToLv95() throws Exception {
    CreateServicePointVersionModel aargauServicePointVersion = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersion.getServicePointGeolocation().setSpatialReference(SpatialReference.LV03);
    aargauServicePointVersion.getServicePointGeolocation().setEast(600127.58303);
    aargauServicePointVersion.getServicePointGeolocation().setNorth(199776.88044);

    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(aargauServicePointVersion)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
        .andExpect(jsonPath("$.number.number", is(8034510)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationOfficial, is("Aargau Strasse")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.sloid, is("ch:1:sloid:18771")))
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
    CreateServicePointVersionModel aargauServicePointVersion = ServicePointTestData.getAargauServicePointVersionModel();
    aargauServicePointVersion.getServicePointGeolocation().setSpatialReference(SpatialReference.WGS84WEB);
    aargauServicePointVersion.getServicePointGeolocation().setEast(828251.335735);
    aargauServicePointVersion.getServicePointGeolocation().setNorth(5933765.900287);

    mvc.perform(post("/v1/service-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(aargauServicePointVersion)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.id, is(servicePointVersion.getId().intValue() + 1)))
        .andExpect(jsonPath("$.number.number", is(8034510)))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.designationOfficial, is("Aargau Strasse")))
        .andExpect(jsonPath("$." + ServicePointVersionModel.Fields.sloid, is("ch:1:sloid:18771")))
        .andExpect(jsonPath("$.servicePointGeolocation.spatialReference", is(WGS84.toString())))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.north", is(46.94907577445)))
        .andExpect(jsonPath("$.servicePointGeolocation.wgs84.east", is(7.44030833983)))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.east", is(2600127.58359)))
        .andExpect(jsonPath("$.servicePointGeolocation.lv95.north", is(1199776.88159)))
        .andExpect(jsonPath("$.hasGeolocation", is(true)));
  }
}