package ch.sbb.atlas.apim.configuration;

import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class OpenApiYamlExporter {

    private static final List<String> EXPORT_DIRS = List.of("api-dev", "api-test", "api-int", "api-prod");

    public void export(OpenAPI openAPI) {
        for (String dir : EXPORT_DIRS) {
            writeToFile(dir, openAPI);
        }
    }

    private void writeToFile(String dir, OpenAPI openAPI) {
        try {
            Yaml.mapper().writeValue(Paths.get("src/main/resources").resolve(dir).resolve("spec.yaml").toFile(), openAPI);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
