package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class TrafficPointElementSloidServiceTest {

  @Autowired
  private TrafficPointElementSloidService trafficPointElementSloidService;

  @Test
  void shouldGenerateNewSloid() {
    String sloid = trafficPointElementSloidService.getNextSloidForPlatform(ServicePointNumber.ofNumberWithoutCheckDigit(8507000));
    assertThat(sloid).isNotNull().startsWith("ch:1:sloid:8507000:0:");
  }
}