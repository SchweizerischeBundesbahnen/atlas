package ch.sbb.atlas.servicepointdirectory;

import ch.sbb.atlas.servicepointdirectory.service.georeference.JourneyPoiClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

  @MockBean
  private JourneyPoiClient journeyPoiClient;

}
