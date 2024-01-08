package ch.sbb.exportservice.config;

import ch.sbb.atlas.configuration.filter.CorrelationIdFilterConfig;
import java.util.Collection;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Import(CorrelationIdFilterConfig.class)
public class AtlasWebMvcConfig implements WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.removeConvertible(String.class, Collection.class);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/**")
        .addResourceLocations("classpath:/static/");
  }
  @Override
  public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
    long timeout = 5 * 60 * 1000;// for example 5 minutes
    WebMvcConfigurer.super.configureAsyncSupport(configurer);
    configurer.setDefaultTimeout(timeout);
  }
}
