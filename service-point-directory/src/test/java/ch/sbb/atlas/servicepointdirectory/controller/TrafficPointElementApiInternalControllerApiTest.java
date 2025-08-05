package ch.sbb.atlas.servicepointdirectory.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.sbb.atlas.location.LocationService;
import ch.sbb.atlas.model.controller.BaseControllerApiTest;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion.Fields;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointVersionRepository;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class TrafficPointElementApiInternalControllerApiTest extends BaseControllerApiTest {

  @MockitoBean
  private CrossValidationService crossValidationService;

  @MockitoBean
  private LocationService locationService;

  private final TrafficPointElementVersionRepository repository;
  private final ServicePointVersionRepository servicePointVersionRepository;

  private TrafficPointElementVersion trafficPointElementVersion;

  @Autowired
  TrafficPointElementApiInternalControllerApiTest(TrafficPointElementVersionRepository repository,
      ServicePointVersionRepository servicePointVersionRepository) {
    this.repository = repository;
    this.servicePointVersionRepository = servicePointVersionRepository;
  }

  @BeforeEach
  void createDefaultVersion() {
    trafficPointElementVersion = TrafficPointTestData.getTrafficPoint();
    this.trafficPointElementVersion = repository.save(trafficPointElementVersion);

    ServicePointVersion servicePointVersion = TrafficPointTestData.testServicePointForTrafficPoint();
    servicePointVersionRepository.save(servicePointVersion);
  }

  @AfterEach
  void cleanUpDb() {
    repository.deleteAll();
    servicePointVersionRepository.deleteAll();
  }

  @Test
  void shouldGetTrafficPointElementValidTodayByServicePointNumber() throws Exception {
    mvc.perform(
            get("/internal/traffic-point-elements/actual-date/" + trafficPointElementVersion.getServicePointNumber().getNumber()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0]." + Fields.sloid, is("ch:1:sloid:1400015:0:310240")));
  }

}
