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
  void shouldGenerateNewSloidInSwitzerland() {
    String sloid = trafficPointElementSloidService.getNextSloidForPlatform(ServicePointNumber.ofNumberWithoutCheckDigit(8507000));
    assertThat(sloid).isNotNull().startsWith("ch:1:sloid:7000:0:");
  }

  @Test
  void shouldGenerateNewSloidNotInSwitzerland() {
    String sloid = trafficPointElementSloidService.getNextSloidForPlatform(ServicePointNumber.ofNumberWithoutCheckDigit(1407000));
    assertThat(sloid).isNotNull().startsWith("ch:1:sloid:1407000:0:");
  }
}