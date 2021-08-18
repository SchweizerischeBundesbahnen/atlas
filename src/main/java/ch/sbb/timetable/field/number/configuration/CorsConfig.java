package ch.sbb.timetable.field.number.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

  @Value("${endpoints.web.cors.path-mappings}")
  private String pathMappings;

  @Value("${endpoints.web.cors.allowed-origins}")
  private String[] allowedOrigins;

  @Value("${endpoints.web.cors.allowed-methods}")
  private String[] allowedMethods;

  @Value("${endpoints.web.cors.allowed-headers}")
  private String[] allowedHeaders;

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping(pathMappings)
            .allowedHeaders(allowedHeaders)
            .allowedOrigins(allowedOrigins)
            .allowedMethods(allowedMethods)
            .allowCredentials(true);
  }
}