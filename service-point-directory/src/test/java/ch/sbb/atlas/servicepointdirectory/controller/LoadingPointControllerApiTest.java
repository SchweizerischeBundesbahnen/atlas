package ch.sbb.atlas.servicepointdirectory.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.servicepoint.CreateLoadingPointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadLoadingPointVersionModel;
import ch.sbb.atlas.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModel;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointCsvModelContainer;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointImportRequestModel;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.repository.LoadingPointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import ch.sbb.atlas.servicepointdirectory.service.loadingpoint.LoadingPointImportService;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

class LoadingPointControllerApiTest extends BaseControllerApiTest {

  private static final int NUMBER = 4201;

  @MockBean
  private CrossValidationService crossValidationServiceMock;

  private final LoadingPointVersionRepository repository;
  private final ServicePointVersionRepository servicePointVersionRepository;
  private final LoadingPointController loadingPointController;

  private LoadingPointVersion loadingPointVersion;
  private ServicePointVersion servicePointVersion;

  @Autowired
   LoadingPointControllerApiTest(LoadingPointVersionRepository repository, ServicePointVersionRepository servicePointVersionRepository,
      LoadingPointController loadingPointController) {
    this.repository = repository;
    this.servicePointVersionRepository = servicePointVersionRepository;
    this.loadingPointController = loadingPointController;
  }

  @BeforeEach
  void createDefaultVersion() {
    servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.createAbroadServicePointVersion());

    LoadingPointVersion loadingPointVersion = LoadingPointVersion
        .builder()
        .number(NUMBER)
        .designation("Piazzale")
        .designationLong("Piazzaleee")
        .connectionPoint(false)
        .servicePointNumber(servicePointVersion.getNumber())
        .validFrom(LocalDate.of(2018, 6, 28))
        .validTo(LocalDate.of(2099, 12, 31))
        .creator("fs45117")
        .creationDate(LocalDateTime.of(2017, 12, 4, 13, 11, 3))
        .editor("GSU_DIDOK")
        .editionDate(LocalDateTime.of(2018, 6, 28, 11, 48, 56))
        .build();

    this.loadingPointVersion = repository.save(loadingPointVersion);
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldGetLoadingPoint() throws Exception {
    int servicePointNumber = servicePointVersion.getNumber().getValue();
    Integer number = loadingPointVersion.getNumber();
    mvc.perform(get("/v1/loading-points/"+servicePointNumber+"/"+ number)).andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + Fields.id, is(loadingPointVersion.getId().intValue())))
        .andExpect(jsonPath("$[0]." + Fields.number, is(NUMBER)))
        .andExpect(jsonPath("$[0]." + Fields.connectionPoint, is(false)))
        .andExpect(jsonPath("$[0].servicePointNumber.number", is(ServicePointNumber.ofNumberWithoutCheckDigit(servicePointNumber).getNumber())))
        .andExpect(jsonPath("$[0].creationDate", is("2017-12-04T13:11:03")))
        .andExpect(jsonPath("$[0].creator", is("fs45117")));
  }

  @Test
  void shouldGetLoadingPointVersionsWithoutFilter() throws Exception {
    mvc.perform(get("/v1/loading-points"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.totalCount", is(1)))
         .andExpect(jsonPath("$.objects[0]." + Fields.id, is(loadingPointVersion.getId().intValue())));
  }

  @Test
  void shouldGetLoadingPointVersionsWithFilter() throws Exception {
    mvc.perform(get("/v1/loading-points" +
                    "?numbers=4201" +
                    "&servicePointSloids=ch:1:sloid:19768" +
                    "&servicePointUicCountryCodes=58" +
                    "&servicePointNumbersShorts=1976" +
                    "8&servicePointNumbers=5819768" +
                    "&sboid=ch:1:sboid:100626" +
                    "&fromDate=" + loadingPointVersion.getValidFrom() +
                    "&toDate=" + loadingPointVersion.getValidTo()+
                    "&validOn=" + LocalDate.of(2020, 6, 28) +
                    "&createdAfter=" + loadingPointVersion.getCreationDate().minusSeconds(1) +
                    "&modifiedAfter=" + loadingPointVersion.getEditionDate()))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.totalCount", is(1)))
         .andExpect(jsonPath("$.objects[0]." + Fields.id, is(loadingPointVersion.getId().intValue())));
  }
  @Test
  void shouldGetLoadingPointVersionsWithArrayInFilter() throws Exception {
    mvc.perform(get("/v1/loading-points" +
                    "?numbers=4201&numbers=1000" +
                    "&servicePointSloids=ch:1:sloid:19768&servicePointSloids=ch:1:sloid:19769" +
                    "&servicePointUicCountryCodes=58&servicePointUicCountryCodes=85" +
                    "&servicePointNumbersShorts=19768&servicePointNumbersShorts=12768" +
                    "&servicePointNumbers=5819768&servicePointNumbers=5819768" +
                    "&sboid=ch:1:sboid:100626&sboid=ch:1:sboid:100628" +
                    "&fromDate=" + loadingPointVersion.getValidFrom() +
                    "&toDate=" + loadingPointVersion.getValidTo()+
                    "&validOn=" + LocalDate.of(2020, 6, 28) +
                    "&createdAfter=" + loadingPointVersion.getCreationDate().minusSeconds(1) +
                    "&modifiedAfter=" + loadingPointVersion.getEditionDate()))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.totalCount", is(1)))
         .andExpect(jsonPath("$.objects[0]." + Fields.id, is(loadingPointVersion.getId().intValue())));
  }

  @Test
  void shouldNotGetLoadingPointVersionsWithFilter() throws Exception {
    mvc.perform(get("/v1/loading-points?numbers=1000"))
         .andExpect(status().isOk())
         .andExpect(jsonPath("$.totalCount", is(0)));
  }

  @Test
  void shouldGetLoadingPointVersionById() throws Exception {
    mvc.perform(get("/v1/loading-points/versions/" + loadingPointVersion.getId())).andExpect(status().isOk());
  }

  @Test
  void shouldFailOnInvalidLoadingPointNumber() throws Exception {
    mvc.perform(get("/v1/loading-points/versions/9123"))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldImportLoadingPointsSuccessfully() throws Exception {
    Mockito.doNothing().when(crossValidationServiceMock).validateServicePointNumberExists(any());

    try (InputStream csvStream = this.getClass().getResourceAsStream("/LADESTELLEN_VERSIONING.csv")) {
      // given
      List<LoadingPointCsvModel> loadingPointCsvModels = LoadingPointImportService.parseLoadingPoints(csvStream);
      List<LoadingPointCsvModel> loadingPointCsvModelsOrderedByValidFrom = loadingPointCsvModels.stream()
          .sorted(Comparator.comparing(BaseDidokCsvModel::getValidFrom))
          .toList();
      LoadingPointImportRequestModel importRequestModel = new LoadingPointImportRequestModel(
          List.of(
              LoadingPointCsvModelContainer
                  .builder()
                  .didokCode(83017186)
                  .loadingPointNumber(4600)
                  .csvModelList(loadingPointCsvModelsOrderedByValidFrom)
                  .build()
          )
      );
      String jsonString = mapper.writeValueAsString(importRequestModel);

      // when
      mvc.perform(post("/v1/loading-points/import")
              .content(jsonString)
              .contentType(contentType))
          // then
          .andExpect(status().isOk())
          .andExpect(jsonPath("$", hasSize(2)));
    }
  }

  @Test
  void shouldReturnBadRequestOnEmptyListImportRequest() throws Exception {
    // given
    LoadingPointImportRequestModel importRequestModel = new LoadingPointImportRequestModel(Collections.emptyList());
    String jsonString = mapper.writeValueAsString(importRequestModel);

    // when
    mvc.perform(post("/v1/loading-points/import")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")));
  }

  @Test
  void shouldReturnBadRequestOnNullListImportRequest() throws Exception {
    // given
    LoadingPointImportRequestModel importRequestModel = new LoadingPointImportRequestModel();
    String jsonString = mapper.writeValueAsString(importRequestModel);

    // when
    mvc.perform(post("/v1/loading-points/import")
            .content(jsonString)
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")));
  }

  @Test
  void shouldReturnBadRequestOnNullImportRequestModel() throws Exception {
    // given & when
    mvc.perform(post("/v1/loading-points/import")
            .contentType(contentType))
        // then
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldCreateLoadingPointVersion() throws Exception {
    CreateLoadingPointVersionModel ladestationOne = CreateLoadingPointVersionModel
        .builder()
        .number(2201)
        .designation("Ladest Nr.1")
        .designationLong("Ladestation Nummer 1")
        .connectionPoint(false)
        .servicePointNumber(servicePointVersion.getNumber().getNumber())
        .validFrom(LocalDate.of(2018, 6, 28))
        .validTo(LocalDate.of(2099, 12, 31))
        .build();

    mvc.perform(post("/v1/loading-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(ladestationOne)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(notNullValue())))
        .andExpect(jsonPath("$.number", is(2201)))
        .andExpect(jsonPath("$.designation", is("Ladest Nr.1")))
        .andExpect(jsonPath("$.designationLong", is("Ladestation Nummer 1")))
        .andExpect(jsonPath("$.servicePointNumber.number", is(5819768)))
        .andExpect(jsonPath("$.connectionPoint", is(false)))
        .andExpect(jsonPath("$.validFrom", is("2018-06-28")))
        .andExpect(jsonPath("$.validTo", is("2099-12-31")))
        .andExpect(jsonPath("$.creator", is("e123456")));
  }

  @Test
  void shouldNotCreateLoadingPointVersionIfCorrespondingServicePointDoesNotExist() throws Exception {
    Mockito.doThrow(new ServicePointNumberNotFoundException(ServicePointNumber.ofNumberWithoutCheckDigit(11_00703)))
        .when(crossValidationServiceMock).validateServicePointNumberExists(any());

    CreateLoadingPointVersionModel ladestationOne = CreateLoadingPointVersionModel
        .builder()
        .number(2201)
        .designation("Ladest Nr.1")
        .designationLong("Ladestation Nummer 1")
        .connectionPoint(false)
        .servicePointNumber(11_00703)
        .validFrom(LocalDate.of(2018, 6, 28))
        .validTo(LocalDate.of(2099, 12, 31))
        .build();

    mvc.perform(post("/v1/loading-points")
            .contentType(contentType)
            .content(mapper.writeValueAsString(ladestationOne)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldUpdateExistingLoadingPointVersionByVersioningInTheMiddle() throws Exception {
    CreateLoadingPointVersionModel update = CreateLoadingPointVersionModel
        .builder()
        .number(loadingPointVersion.getNumber())
        .servicePointNumber(loadingPointVersion.getServicePointNumber().getNumber())
        .designation("Pizzale")
        .designationLong("Piazzaleee")
        .connectionPoint(false)
        .validFrom(LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2020, 12, 31))
        .build();

    mvc.perform(put("/v1/loading-points/" + loadingPointVersion.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(update)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id", is(notNullValue())))
        .andExpect(jsonPath("$[0].designation", is("Piazzale")))
        .andExpect(jsonPath("$[0].validFrom", is("2018-06-28")))
        .andExpect(jsonPath("$[0].validTo", is("2019-12-31")))
        .andExpect(jsonPath("$[1].id", is(notNullValue())))
        .andExpect(jsonPath("$[1].designation", is("Pizzale")))
        .andExpect(jsonPath("$[1].validFrom", is("2020-01-01")))
        .andExpect(jsonPath("$[1].validTo", is("2020-12-31")))
        .andExpect(jsonPath("$[2].id", is(notNullValue())))
        .andExpect(jsonPath("$[2].designation", is("Piazzale")))
        .andExpect(jsonPath("$[2].validFrom", is("2021-01-01")))
        .andExpect(jsonPath("$[2].validTo", is("2099-12-31")));
  }

  @Test
  void shouldReturnOptimisticLockingErrorResponse() throws Exception {
    //given
    ReadLoadingPointVersionModel currentLoadingPoint =
        loadingPointController.getLoadingPointVersion(loadingPointVersion.getId());

    // When first update it is ok
    CreateLoadingPointVersionModel update = CreateLoadingPointVersionModel
        .builder()
        .id(currentLoadingPoint.getId())
        .number(currentLoadingPoint.getNumber())
        .servicePointNumber(currentLoadingPoint.getServicePointNumber().getNumber())
        .designation("Pitzale")
        .designationLong(currentLoadingPoint.getDesignationLong())
        .connectionPoint(currentLoadingPoint.isConnectionPoint())
        .validFrom(currentLoadingPoint.getValidFrom())
        .validTo(currentLoadingPoint.getValidTo())
        .etagVersion(currentLoadingPoint.getEtagVersion())
        .build();

    mvc.perform(put("/v1/loading-points/" + currentLoadingPoint.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(update)))
        .andExpect(status().isOk());

    // Then on a second update it has to return error for optimistic lock
    update.setDesignationLong("New and hot loadingpoint");
    MvcResult mvcResult = mvc.perform(put("/v1/loading-points/" + currentLoadingPoint.getId())
            .contentType(contentType)
            .content(mapper.writeValueAsString(update)))
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
  void shouldFailOnFindWithInvalidServicePointNumber() throws Exception {
    mvc.perform(get("/v1/loading-points?servicePointNumbers=12345678"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is("Constraint for requestbody was violated")))
        .andExpect(jsonPath("$.details[0].message",
            is("Value 12345678 rejected due to must be less than or equal to 9999999")));
  }

}
