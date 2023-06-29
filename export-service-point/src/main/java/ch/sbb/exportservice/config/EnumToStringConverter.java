package ch.sbb.exportservice.config;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allow to pass case-insensitive Enum to @PathParameter
 */
@Configuration
public class EnumToStringConverter implements WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    ApplicationConversionService.configure(registry);
  }
}