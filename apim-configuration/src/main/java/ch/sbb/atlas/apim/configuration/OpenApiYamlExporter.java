package ch.sbb.atlas.apim.configuration;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OpenApiYamlExporter {

  public static void export(OpenAPI openAPI, String targetDir) {
    writeToFile(targetDir, openAPI);
  }

  private static void writeToFile(String dir, OpenAPI openAPI) {
    try {
      Path targetDir = SpecFilePath.getBasePath().resolve(dir);
      Files.createDirectories(targetDir);
      File file = targetDir.resolve("spec.yaml").toFile();
      Yaml.mapper().writeValue(file, openAPI);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
