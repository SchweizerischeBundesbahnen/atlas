package ch.sbb.atlas.servicepointdirectory.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.base.service.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.ServicePointTestData;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointImportReqModel;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointVersionModel.Fields;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointCsvModel;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointImportService;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ServicePointControllerApiTest extends BaseControllerApiTest {

  private static final String CSV_FILE = "DIDOK3_DIENSTSTELLEN_ALL_V_3_20221222015634.csv";

  private final ServicePointVersionRepository repository;
  private ServicePointVersion servicePointVersion;

  @Autowired
  public ServicePointControllerApiTest(ServicePointVersionRepository repository) {
    this.repository = repository;
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

  @Test
  void test_ImportServicePoints_shouldWork() throws Exception {
    InputStream csvStream = this.getClass().getResourceAsStream("/" + CSV_FILE);
    List<ServicePointCsvModel> servicePointCsvModels = ServicePointImportService.parseServicePoints(csvStream);
    servicePointCsvModels = servicePointCsvModels.subList(0, 10);

    ServicePointImportReqModel importReqModel = new ServicePointImportReqModel(servicePointCsvModels);

    String s = mapper.writeValueAsString(importReqModel);

    mvc.perform(post("/v1/service-points/import")
            .content(s)
            .contentType(contentType))
        .andExpect(status().isOk());
  }

}
