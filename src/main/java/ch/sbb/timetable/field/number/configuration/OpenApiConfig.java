package ch.sbb.timetable.field.number.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
    final String oauthProvider = "Azure Ad";
    return new OpenAPI()
        .addServersItem(new Server().url("/"))
        .info(new Info()
            .title("Timetable Field Number API")
            .description("This is the API for all your Timetable Field Number needs")
            .contact(new Contact().name("ATLAS Team")
                                  .url(
                                      "https://confluence.sbb.ch/display/ATLAS/ATLAS+-+SKI+Business+Platform")
                                  .email("TechSupport-ATLAS@sbb.ch"))
            .version(version))
        .components(new Components()
            .addSecuritySchemes(oauthProvider, new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .description("OAuth2 Security Scheme")
                .flows(new OAuthFlows()
                    .authorizationCode(new OAuthFlow()
                        .authorizationUrl(
                            "https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/oauth2/v2.0/authorize")
                        .tokenUrl(
                            "https://login.microsoftonline.com/2cda5d11-f0ac-46b3-967d-af1b2e1bd01a/oauth2/v2.0/token")
                        .scopes(new Scopes().addString("default", "api://fa0c4e69-d22b-47ff-b71d-af9493919cc6/.default"))
                    ))))
        .addSecurityItem(new SecurityRequirement().addList(oauthProvider));
  }
}
