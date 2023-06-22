package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepointdirectory.TrafficPointTestData;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class TrafficPointVersioningTest {

  private final TrafficPointElementService trafficPointElementService;

  @Autowired
  TrafficPointVersioningTest(TrafficPointElementService trafficPointElementService) {
    this.trafficPointElementService = trafficPointElementService;
  }

  @Test
  void shouldMergeTrafficPoint() {
    // given
    TrafficPointElementVersion trafficPointElementVersion = TrafficPointTestData.getBasicTrafficPoint();
    TrafficPointElementVersion saved = trafficPointElementService.save(trafficPointElementVersion);

    TrafficPointElementVersion edited = TrafficPointTestData.getBasicTrafficPoint();
    edited.setValidFrom(LocalDate.of(2024, 1, 2));
    edited.setValidTo(LocalDate.of(2024, 12, 31));
    edited.getTrafficPointElementGeolocation().setCreationDate(null);
    edited.getTrafficPointElementGeolocation().setCreator(null);
    edited.getTrafficPointElementGeolocation().setEditionDate(null);
    edited.getTrafficPointElementGeolocation().setEditor(null);
    // when
    trafficPointElementService.updateTrafficPointElementVersion(edited);

    // then
    assertThat(trafficPointElementService.findTrafficPointElements("ch:1:sloid:123")).hasSize(1);
  }

}
