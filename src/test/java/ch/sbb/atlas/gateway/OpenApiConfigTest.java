package ch.sbb.atlas.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("integration-test")
class OpenApiConfigTest {

  @Value("classpath:apis/lidiApi.json")
  private Resource lidiApi;

  @Value("classpath:apis/ttfnApi.json")
  private Resource ttfnApi;

  @Value("classpath:apis/combinedApi.json")
  private Resource combinedApi;

  private final OpenApiConfig openApiConfig = new OpenApiConfig();

  @Test
  void shouldCombineOpenApis() throws IOException {
    // Given
    Map<String, OpenAPI> openApis = new HashMap<>();
    openApis.put("timetable-field-number", getOpenApiFromResource(ttfnApi));
    openApis.put("line-directory", getOpenApiFromResource(lidiApi));

    OpenAPI expected = getOpenApiFromResource(combinedApi);

    // When
    OpenAPI combinedApi = openApiConfig.getCombinedApi(openApis);

    // Then
    assertThat(combinedApi).isEqualTo(expected);
  }

  private OpenAPI getOpenApiFromResource(Resource resource) throws IOException {
    return Json.mapper().readValue(resource.getInputStream(), OpenAPI.class);
  }
}