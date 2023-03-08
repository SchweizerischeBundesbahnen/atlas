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
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpenApiLoader {

  private final ProductionConfiguration productiveApiConfiguration;

  public Map<String, OpenAPI> loadAllApis() {
    return loadOpenApis(false);
  }

  public Map<String, OpenAPI> loadProductiveApisOnly() {
    return loadOpenApis(true);
  }
  private Map<String, OpenAPI> loadOpenApis(boolean includeProductiveApisOnly) {
    Map<String, OpenAPI> result = new HashMap<>();
    Path sourcePath = Paths.get("src/main/resources/apis");
    log.info("From Source Path {}", sourcePath.toAbsolutePath());
    try (Stream<Path> pathStream = Files.walk(sourcePath)) {
      List<File> apiSpecs = pathStream.filter(Files::isRegularFile)
              .map(Path::toFile).toList();
      log.info("Found {} OpenAPI specs", apiSpecs.size());
      if (apiSpecs.isEmpty()) {
        throw new IllegalStateException("No OpenAPI specs found!");
      }
      for (File apiSpec : apiSpecs) {
        InputStream inputStream = new FileInputStream(apiSpec);
        OpenAPI openAPI = Yaml.mapper().readValue(inputStream, OpenAPI.class);
        String apiServiceName = apiSpec.getParentFile().getName();
        if (!includeProductiveApisOnly || productiveApiConfiguration.getApis()
                                                                    .contains(apiServiceName)) {
          log.info("Loaded OpenAPI spec for {}", apiServiceName);
          result.put(apiServiceName, openAPI);
        }
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    return result;
  }
}
