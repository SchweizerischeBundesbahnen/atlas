package ch.sbb.atlas.apim.configuration;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.nio.file.Paths;

@UtilityClass
public class OpenApiYamlExporter {

  public static void export(OpenAPI openAPI, String targetDir) {
      writeToFile(targetDir, openAPI);
  }

  private static void writeToFile(String dir, OpenAPI openAPI) {
    try {
      Yaml.mapper()
          .writeValue(Paths.get("src/main/resources").resolve(dir).resolve("spec.yaml").toFile(),
              openAPI);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
