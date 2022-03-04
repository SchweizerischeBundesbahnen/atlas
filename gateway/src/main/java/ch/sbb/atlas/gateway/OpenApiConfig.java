package ch.sbb.atlas.gateway;


import feign.Feign;
import feign.FeignException;
import feign.Response;
import feign.codec.Decoder;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
@EnableFeignClients
@Profile("!integration-test")
public class OpenApiConfig {

  private static final String NEWLINE = "<br/>";

  @Value("${info.app.version}")
  private String version;

  @Bean
  public OpenAPI openApi(RouteLocator routeLocator) {
    return getCombinedApi(loadOpenApis(routeLocator));
  }

  OpenAPI getCombinedApi(Map<String, OpenAPI> openApis) {
    return createAtlasApi(openApis).components(combineComponents(openApis))
                                   .paths(combinePaths(openApis));
  }

  private OpenAPI createAtlasApi(Map<String, OpenAPI> apis) {
    StringBuilder description = new StringBuilder().append(
                                                       "This is the API for all your needs SKI core data")
                                                   .append(NEWLINE)
                                                   .append(NEWLINE)
                                                   .append(
                                                       "Atlas serves the following applications:")
                                                   .append(
                                                       NEWLINE);
    apis.forEach((application, api) -> description.append(application)
                                                  .append(":")
                                                  .append(api.getInfo().getVersion())
                                                  .append(NEWLINE));
    return new OpenAPI()
        .addServersItem(new Server().url("/"))
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
      openAPI.getComponents().getSchemas().forEach(components::addSchemas);
    }
    return components;
  }

  private Paths combinePaths(Map<String, OpenAPI> apis) {
    Paths paths = new Paths();
    for (Entry<String, OpenAPI> openAPI : apis.entrySet()) {
      openAPI.getValue()
             .getPaths()
             .forEach((key, value) -> paths.addPathItem("/" + openAPI.getKey() + key, value));
    }
    return paths;
  }

  private Map<String, OpenAPI> loadOpenApis(RouteLocator routeLocator) {
    List<Route> routes = Objects.requireNonNull(routeLocator.getRoutes().collectList().block());
    Map<String, OpenAPI> openApis = new HashMap<>();
    routes.forEach(route -> openApis.put(route.getId(), getOpenApi(route.getUri().toString())));
    return openApis;
  }

  private OpenAPI getOpenApi(String uri) {
    return Feign.builder()
                .contract(new SpringMvcContract())
                .decoder(new OpenApiDecoder())
                .target(OpenApiSpecClient.class, uri)
                .getApi();
  }

  @FeignClient(name = "OpenApiSpecClient")
  public interface OpenApiSpecClient {

    @GetMapping(value = "v3/api-docs")
    OpenAPI getApi();
  }

  private static class OpenApiDecoder implements Decoder {

    @Override
    public Object decode(Response response, Type type) throws IOException, FeignException {
      return Json.mapper().readValue(response.body().asInputStream(), OpenAPI.class);
    }
  }
}
