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
  void shouldGenerateNewSloidInSwitzerlandForPlatform() {
    String sloid = trafficPointElementSloidService.getNextSloid(ServicePointNumber.ofNumberWithoutCheckDigit(8507000), false);
    assertThat(sloid).isNotNull().startsWith("ch:1:sloid:7000:0:");
  }

  @Test
  void shouldGenerateNewSloidNotInSwitzerlandForPlatform() {
    String sloid = trafficPointElementSloidService.getNextSloid(ServicePointNumber.ofNumberWithoutCheckDigit(1407000), false);
    assertThat(sloid).isNotNull().startsWith("ch:1:sloid:1407000:0:");
  }

  @Test
  void shouldGenerateNewSloidInSwitzerlandForArea() {
    String sloid = trafficPointElementSloidService.getNextSloid(ServicePointNumber.ofNumberWithoutCheckDigit(8507000), true);
    assertThat(sloid).isNotNull().startsWith("ch:1:sloid:7000:");
  }

  @Test
  void shouldGenerateNewSloidNotInSwitzerlandForArea() {
    String sloid = trafficPointElementSloidService.getNextSloid(ServicePointNumber.ofNumberWithoutCheckDigit(1407000), true);
    assertThat(sloid).isNotNull().startsWith("ch:1:sloid:1407000:");
  }
}