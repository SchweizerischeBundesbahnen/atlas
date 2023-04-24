package ch.sbb.atlas.apim.configuration;

import ch.sbb.atlas.apim.configuration.StagePublishingConfiguration.StageConfig;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class OpenApiMerger {

    private static final String NEWLINE = "<br/>";
    private final String version;
    private final String stage;
    private final StageConfig stageConfig;

    public OpenAPI getCombinedApi(Map<String, OpenAPI> openApis) {
        return createAtlasApi(openApis).components(combineComponents(openApis))
                .paths(combinePaths(openApis));
    }

    private OpenAPI createAtlasApi(Map<String, OpenAPI> apis) {
        StringBuilder description = new StringBuilder().append(
                        "This is the API for all your needs SKI core data")
                .append(NEWLINE)
                .append(NEWLINE)
                .append("Atlas serves the following applications:")
                .append(NEWLINE);
        description.append("<ul>");
        apis.forEach((application, api) -> {
            String restDocLocation = "https://" + application + "." + stage + ".app.sbb.ch/static/rest-api.html";
            description
                    .append("<li>")
                    .append(application)
                    .append(":")
                    .append(api.getInfo().getVersion())
                    .append(NEWLINE)
                    .append("RestDoc: ")
                    .append("<a href='").append(restDocLocation).append("' target='_blank'>").append(restDocLocation).append("</a>")
                    .append("</li>");
        });
        description.append("</ul>");

        return new OpenAPI()
                .info(new Info()
                        .title("Atlas API")
                        .description(description.toString())
                        .contact(new Contact().name("ATLAS Team")
                                .url(
                                        "https://confluence.sbb.ch/display/ATLAS/ATLAS+-+SKI+Business+Platform")
                                .email("TechSupport-ATLAS@sbb.ch"))
                        .version(version));
    }

    private Components combineComponents(Map<String, OpenAPI> apis) {
        Components components = new Components();
        for (OpenAPI openAPI : apis.values()) {
            openAPI.getComponents().getSchemas().entrySet().stream()
                .filter(entry -> stageConfig.isNotExcluded(entry.getKey()))
                .forEach(entry -> components.addSchemas(entry.getKey(), entry.getValue()));
        }
        return components;
    }

    private Paths combinePaths(Map<String, OpenAPI> apis) {
        Paths paths = new Paths();
        for (Map.Entry<String, OpenAPI> openAPI : apis.entrySet()) {
            openAPI.getValue()
                    .getPaths()
                    .forEach((path, value) -> {
                        if (stageConfig.isNotExcluded(path)) {
                            paths.addPathItem("/" + openAPI.getKey() + path, value);
                        }
                    });
        }
        return paths;
    }
}
