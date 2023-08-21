package ch.sbb.atlas.servicepointdirectory.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

public class LoadingPointControllerApiTest extends BaseControllerApiTest {

  @MockBean
  private CrossValidationService crossValidationServiceMock;

  private final LoadingPointVersionRepository repository;
  private final ServicePointVersionRepository servicePointVersionRepository;

  private LoadingPointVersion loadingPointVersion;
  private ServicePointVersion servicePointVersion;

  @Autowired
  public LoadingPointControllerApiTest(LoadingPointVersionRepository repository, ServicePointVersionRepository servicePointVersionRepository) {
    this.repository = repository;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    servicePointVersion = servicePointVersionRepository.save(ServicePointTestData.createAbroadServicePointVersion());

    LoadingPointVersion loadingPointVersion = LoadingPointVersion
        .builder()
        .number(4201)
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
        .andExpect(jsonPath("$[0]." + Fields.number, is(4201)))
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
                    "8&servicePointNumbers=58197681" +
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
                    "?numbers=4201&numbers=0001" +
                    "&servicePointSloids=ch:1:sloid:19768&servicePointSloids=ch:1:sloid:19769" +
                    "&servicePointUicCountryCodes=58&servicePointUicCountryCodes=85" +
                    "&servicePointNumbersShorts=19768&servicePointNumbersShorts=12768" +
                    "&servicePointNumbers=58197681&servicePointNumbers=58197687" +
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
    mvc.perform(get("/v1/loading-points/9123"))
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

}
