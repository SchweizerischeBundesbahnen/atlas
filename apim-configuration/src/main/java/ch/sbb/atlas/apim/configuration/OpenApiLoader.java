package ch.sbb.atlas.apim.configuration;

import ch.sbb.atlas.apim.configuration.StagePublishingConfiguration.StageConfig;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OpenApiLoader {

  public Map<String, OpenAPI> loadOpenApis(StageConfig stageConfig) {
    Map<String, OpenAPI> result = new HashMap<>();
    Path sourcePath = SpecFilePath.getBasePath().resolve("apis");
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
        if (stageConfig.getApis().contains(apiServiceName)) {
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
