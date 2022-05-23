package ch.sbb.atlas.apim.configuration;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenApiLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiLoader.class);

  private final ProductionConfiguration productiveApiConfiguration;

  public Map<String, OpenAPI> loadAllApis() {
    return loadOpenApis(false);
  }

  public Map<String, OpenAPI> loadProductiveApisOnly() {
    return loadOpenApis(true);
  }
  private Map<String, OpenAPI> loadOpenApis(boolean includeProductiveApisOnly) {
    Map<String, OpenAPI> result = new HashMap<>();
    try (Stream<Path> pathStream = Files.walk(Paths.get("src/main/resources/apis"))) {
      List<File> apiSpecs = pathStream.filter(Files::isRegularFile)
                                      .map(Path::toFile)
                                      .collect(Collectors.toList());
      LOGGER.info("Found {} OpenAPI specs", apiSpecs.size());
      if (apiSpecs.isEmpty()) {
        throw new IllegalStateException("No OpenAPI specs found!");
      }
      for (File apiSpec : apiSpecs) {
        InputStream inputStream = new FileInputStream(apiSpec);
        OpenAPI openAPI = Yaml.mapper().readValue(inputStream, OpenAPI.class);
        String apiServiceName = apiSpec.getParentFile().getName();
        if (!includeProductiveApisOnly || productiveApiConfiguration.getApis()
                                                                    .contains(apiServiceName)) {
          LOGGER.info("Loaded OpenAPI spec for {}", apiServiceName);
          result.put(apiServiceName, openAPI);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return result;
  }
}
