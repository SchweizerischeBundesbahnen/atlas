package ch.sbb.atlas.gateway;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("integration-test")
class AtlasApiAuthGatewayApplicationTest {

  @Test
  void applicationContextLoads() {
    assertThatNoException().isThrownBy(() -> {});
  }
}