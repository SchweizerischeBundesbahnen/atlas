package ch.sbb.timetable.field.number.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Value("${info.app.version}")
  private String version;

  @Bean
  public GroupedOpenApi defaultApi() {
    return GroupedOpenApi.builder()
                         .group("public")
                         .packagesToScan("ch.sbb.timetable.field.number")
                         .build();
  }

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .addServersItem(new Server().url("/"))
        .info(new Info()
            .title("Timetable Field Number API")
            .description("This is the API for all your Timetable Field Number needs")
            .contact(new Contact().name("ATLAS Team")
                                  .url(
                                      "https://confluence.sbb.ch/display/ATLAS/ATLAS+-+SKI+Business+Platform")
                                  .email("TechSupport-ATLAS@sbb.ch"))
            .version(version));
  }
}
