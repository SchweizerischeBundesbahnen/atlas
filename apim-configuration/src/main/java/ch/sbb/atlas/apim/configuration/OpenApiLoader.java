package ch.sbb.atlas.apim.configuration;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

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

@Component
public class OpenApiLoader {

    @Autowired
    private ResourceLoader resourceLoader;

    public Map<String, OpenAPI> loadOpenApis() {
        Map<String, OpenAPI> result = new HashMap<>();
        try (Stream<Path> pathStream = Files.walk(Paths.get("src/main/resources/apis"))) {
            List<File> apiSpecs = pathStream.filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
            for (File apiSpec : apiSpecs) {
                InputStream inputStream = new FileInputStream(apiSpec);
                OpenAPI openAPI = Json.mapper().readValue(inputStream, OpenAPI.class);
                result.put(apiSpec.getParentFile().getName(), openAPI);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return result;
    }
}
