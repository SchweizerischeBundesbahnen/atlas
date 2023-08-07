package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.TrafficPointElementVersionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public class TrafficPointElementServiceTest {

  // required for test functionality
  @MockBean
  private TrafficPointElementValidationService trafficPointElementValidationService;

  private final TrafficPointElementService trafficPointElementService;
  private final TrafficPointElementVersionRepository trafficPointElementVersionRepository;

  @Autowired
  TrafficPointElementServiceTest(TrafficPointElementService trafficPointElementService,
      TrafficPointElementVersionRepository trafficPointElementVersionRepository) {
    this.trafficPointElementService = trafficPointElementService;
    this.trafficPointElementVersionRepository = trafficPointElementVersionRepository;
  }

  @AfterEach
  void cleanup() {
    trafficPointElementVersionRepository.deleteAll();
  }

  @Test
  void shouldMergeTrafficPoint() {
    // given
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getBasicTrafficPoint();
    trafficPointElementService.save(trafficPointElementVersion);

    TrafficPointElementVersion edited = TrafficPointTestData.getBasicTrafficPoint();
    edited.setValidFrom(LocalDate.of(2024, 1, 2));
    edited.setValidTo(LocalDate.of(2024, 12, 31));
    edited.getTrafficPointElementGeolocation().setCreationDate(null);
    edited.getTrafficPointElementGeolocation().setCreator(null);
    edited.getTrafficPointElementGeolocation().setEditionDate(null);
    edited.getTrafficPointElementGeolocation().setEditor(null);
    // when
    trafficPointElementService.updateTrafficPointElementVersion(trafficPointElementVersion, edited);

    // then
    assertThat(trafficPointElementService.findBySloidOrderByValidFrom("ch:1:sloid:123")).hasSize(1);
  }

}
