package ch.sbb.atlas.servicepointdirectory.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.base.service.imports.servicepoint.BaseDidokCsvModel;
import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.base.service.imports.servicepoint.servicepoint.ServicePointCsvModelContainer;
import ch.sbb.atlas.base.service.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointVersionModel.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicePointControllerApiTest extends BaseControllerApiTest {

  private static final String CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20221222015634.csv";

  private final ServicePointVersionRepository repository;
  private final ServicePointService servicePointService;
  private final ServicePointImportService servicePointImportService;
  private ServicePointVersion servicePointVersion;

  @Autowired
  public ServicePointControllerApiTest(ServicePointVersionRepository repository, ServicePointService servicePointService,
      ServicePointImportService servicePointImportService) {
    this.repository = repository;
    this.servicePointService = servicePointService;
    this.servicePointImportService = servicePointImportService;
  }

  @BeforeEach
  void createDefaultVersion() {
    servicePointVersion = repository.save(ServicePointTestData.getBernWyleregg());
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
  }

  @Test
  void shouldGetServicePoint() throws Exception {
    mvc.perform(get("/v1/service-points/85890087")).andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + Fields.id, is(servicePointVersion.getId().intValue())))
        .andExpect(jsonPath("$[0].number.number", is(8589008)))
        .andExpect(jsonPath("$[0]." + Fields.designationOfficial, is("Bern, Wyleregg")))
        .andExpect(jsonPath("$[0].meansOfTransportInformation[0].code", is("B")))
        .andExpect(jsonPath("$[0].meansOfTransportInformation[0].designationDe", is("Bus")))
        .andExpect(jsonPath("$[0]." + Fields.operatingPointRouteNetwork, is(false)))

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
        .andExpect(jsonPath("$.objects[0]." + Fields.id, is(servicePointVersion.getId().intValue())))
        .andExpect(jsonPath("$.totalCount", is(1)));
  }

  @Test
  void shouldGetServicePointVersionById() throws Exception {
    mvc.perform(get("/v1/service-points/versions/" + servicePointVersion.getId())).andExpect(status().isOk());
  }

  @Test
  void shouldFailOnInvalidServicePointNumber() throws Exception {
    mvc.perform(get("/v1/service-points/123"))
        .andExpect(status().isNotFound());
  }

  /*@Test
  void test_ImportServicePoints_shouldWork() throws Exception {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);
    servicePointCsvModels = servicePointCsvModels.subList(0, 10);
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = new ArrayList<>();
    Map<Integer, List<ServicePointCsvModel>> collect = servicePointCsvModels.stream()
        .collect(Collectors.groupingBy(ServicePointCsvModel::getDidokCode));
    collect.forEach((key, value) -> {
      ServicePointCsvModelContainer servicePointCsvModelContainer = ServicePointCsvModelContainer.builder()
          .didokCode(key)
          .servicePointCsvModelList(value)
          .build();
      value.sort(Comparator.comparing(BaseDidokCsvModel::getValidFrom));
      servicePointCsvModelContainers.add(servicePointCsvModelContainer);
    });

    ServicePointImportReqModel importReqModel = new ServicePointImportReqModel(servicePointCsvModelContainers);

    String s = mapper.writeValueAsString(importReqModel);

    mvc.perform(post("/v1/service-points/import")
            .content(s)
            .contentType(contentType))
        .andExpect(status().isOk());
  }

  @Test
  void test_ImportServicePoints_withVersionsOrdered() throws Exception {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);
    int didokCode = 10000190;
    List<ServicePointCsvModel> servicePointCsvModelsOrdered =
        servicePointCsvModels.stream().filter(item -> item.getDidokCode() == didokCode).sorted(
            (o1, o2) -> o1.getValidFrom().isBefore(o2.getValidFrom()) ? 1 :
                o1.getValidFrom().isEqual(o2.getValidFrom()) ? 0 : -1
        ).toList();
    List<ServicePointCsvModelContainer> servicePointCsvModelContainers = new ArrayList<>();
    ServicePointCsvModelContainer servicePointCsvModelContainer = ServicePointCsvModelContainer.builder()
        .didokCode(didokCode).servicePointCsvModelList(servicePointCsvModelsOrdered).build();
    servicePointCsvModelContainers.add(servicePointCsvModelContainer);

    ServicePointImportReqModel importReqModel = new ServicePointImportReqModel(servicePointCsvModelContainers);

    String s = mapper.writeValueAsString(importReqModel);

    mvc.perform(post("/v1/service-points/import")
            .content(s)
            .contentType(contentType))
        .andExpect(status().isOk());

    ServicePointNumber servicePointNumber = ServicePointNumber.of(didokCode);
    List<ServicePointVersion> savedServicePointsOrderedValidFrom = servicePointService.findServicePoint(servicePointNumber);
    repository.deleteAll();

    // second import
    List<ServicePointCsvModel> servicePointCsvModelsReverseOrdered =
        servicePointCsvModels.stream().filter(item -> item.getDidokCode() == didokCode).sorted(
            (o1, o2) -> o1.getValidFrom().isAfter(o2.getValidFrom()) ? 1 :
                o1.getValidFrom().isEqual(o2.getValidFrom()) ? 0 : -1
        ).toList();

    List<ServicePointCsvModelContainer> servicePointCsvModelContainersReverseOrdered = new ArrayList<>();
    ServicePointCsvModelContainer servicePointCsvModelContainerReverseOrdered = ServicePointCsvModelContainer.builder()
        .didokCode(didokCode).servicePointCsvModelList(servicePointCsvModelsReverseOrdered).build();
    servicePointCsvModelContainers.add(servicePointCsvModelContainerReverseOrdered);

    importReqModel = new ServicePointImportReqModel(servicePointCsvModelContainersReverseOrdered);
    s = mapper.writeValueAsString(importReqModel);

    mvc.perform(post("/v1/service-points/import")
            .content(s)
            .contentType(contentType))
        .andExpect(status().isOk());

    List<ServicePointVersion> savedServicePointsReverseOrderedValidFrom =
        servicePointService.findServicePoint(servicePointNumber);

    // asserts
    assertThat(savedServicePointsOrderedValidFrom).hasSize(servicePointCsvModelsOrdered.size());
    assertThat(savedServicePointsReverseOrderedValidFrom).hasSize(servicePointCsvModelsReverseOrdered.size());
    assertThat(savedServicePointsOrderedValidFrom).hasSize(savedServicePointsReverseOrderedValidFrom.size());
    for (int i = 0; i < savedServicePointsOrderedValidFrom.size(); i++) {
      assertThat(savedServicePointsOrderedValidFrom.get(i).getValidFrom()
          .isEqual(savedServicePointsReverseOrderedValidFrom.get(i).getValidFrom())).isTrue();
      assertThat(savedServicePointsOrderedValidFrom.get(i).getValidTo()
          .isEqual(savedServicePointsReverseOrderedValidFrom.get(i).getValidTo())).isTrue();
      assertThat(savedServicePointsOrderedValidFrom.get(i).getDesignationOfficial()).isEqualTo(
          savedServicePointsReverseOrderedValidFrom.get(i).getDesignationOfficial());
    }
  }*/

  @Test
  void test_ImportServicePoints_withVersionsUnordered() throws Exception {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);

    int didokCode = 10000190;
    List<ServicePointCsvModel> servicePointCsvModelsOrdered =
        servicePointCsvModels.stream().filter(item -> item.getDidokCode() == didokCode).sorted(
            Comparator.comparing(BaseDidokCsvModel::getValidFrom)).toList();

    ServicePointImportReqModel importReqModel =
        new ServicePointImportReqModel(List.of(
            ServicePointCsvModelContainer.builder().servicePointCsvModelList(servicePointCsvModelsOrdered).didokCode(didokCode)
                .build()));

    String s = mapper.writeValueAsString(importReqModel);

    mvc.perform(post("/v1/service-points/import")
            .content(s)
            .contentType(contentType))
        .andExpect(status().isOk());

    // update with ordered list
    LocalDate newValidFrom = servicePointCsvModelsOrdered.get(0).getValidFrom().minusDays(5);
    servicePointCsvModelsOrdered.get(0).setValidFrom(newValidFrom);
    LocalDate newValidTo = servicePointCsvModelsOrdered.get(1).getValidTo().plusDays(5);
    servicePointCsvModelsOrdered.get(1).setValidTo(newValidTo);

    servicePointCsvModelsOrdered.get(2).setBezeichnungOffiziell("Test");

    servicePointCsvModelsOrdered.get(4).setBezeichnungOffiziell("Test2");
    servicePointCsvModelsOrdered.get(4).setValidTo(servicePointCsvModelsOrdered.get(4).getValidTo().minusDays(5));

    /*importReqModel = new ServicePointImportReqModel(servicePointCsvModelsOrdered);

    s = mapper.writeValueAsString(importReqModel);

    mvc.perform(post("/v1/service-points/import")
            .content(s)
            .contentType(contentType))
        .andExpect(status().isOk());

    ServicePointNumber servicePointNumber = ServicePointNumber.of(didokCode);
    List<ServicePointVersion> updatedWithOrderedList = servicePointService.findServicePoint(servicePointNumber);
    repository.deleteAll();

    // update with unordered list
    servicePointCsvModelsOrdered =
        servicePointCsvModels.stream().filter(item -> item.getDidokCode() == didokCode).sorted(
            (o1, o2) -> o1.getValidFrom().isBefore(o2.getValidFrom()) ? 1 :
                o1.getValidFrom().isEqual(o2.getValidFrom()) ? 0 : -1
        ).toList();
    importReqModel = new ServicePointImportReqModel(servicePointCsvModelsOrdered);

    s = mapper.writeValueAsString(importReqModel);

    mvc.perform(post("/v1/service-points/import")
            .content(s)
            .contentType(contentType))
        .andExpect(status().isOk());

    // update
    newValidFrom = servicePointCsvModelsOrdered.get(0).getValidFrom().minusDays(5);
    servicePointCsvModelsOrdered.get(0).setValidFrom(newValidFrom);
    newValidTo = servicePointCsvModelsOrdered.get(1).getValidTo().plusDays(5);
    servicePointCsvModelsOrdered.get(1).setValidTo(newValidTo);

    servicePointCsvModelsOrdered.get(2).setBezeichnungOffiziell("Test");

    servicePointCsvModelsOrdered.get(4).setBezeichnungOffiziell("Test2");
    servicePointCsvModelsOrdered.get(4).setValidTo(servicePointCsvModelsOrdered.get(4).getValidTo().minusDays(5));

    // reverse
    List<ServicePointCsvModel> reversedUpdateList = servicePointCsvModelsOrdered.stream().sorted(
        (o1, o2) -> o1.getValidFrom().isAfter(o2.getValidFrom()) ? 1 :
            o1.getValidFrom().isEqual(o2.getValidFrom()) ? 0 : -1
    ).toList();

    importReqModel = new ServicePointImportReqModel(reversedUpdateList);

    s = mapper.writeValueAsString(importReqModel);

    mvc.perform(post("/v1/service-points/import")
            .content(s)
            .contentType(contentType))
        .andExpect(status().isOk());

    List<ServicePointVersion> updatedWithReversedList = servicePointService.findServicePoint(servicePointNumber);

    // asserts
    assertThat(updatedWithOrderedList).hasSize(updatedWithReversedList.size());
    for (int i = 0; i < updatedWithOrderedList.size(); i++) {
      assertThat(updatedWithOrderedList.get(i).getValidFrom()
          .isEqual(updatedWithReversedList.get(i).getValidFrom())).isTrue();
      assertThat(updatedWithOrderedList.get(i).getValidTo()
          .isEqual(updatedWithReversedList.get(i).getValidTo())).isTrue();
      assertThat(updatedWithOrderedList.get(i).getDesignationOfficial()).isEqualTo(
          updatedWithReversedList.get(i).getDesignationOfficial());
    }*/
  }

}
