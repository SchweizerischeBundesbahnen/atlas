package ch.sbb.prm.directory.configuration;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class SwaggerBeanConfig {

  public SwaggerBeanConfig(MappingJackson2HttpMessageConverter converter) {
    List<MediaType> supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
    supportedMediaTypes.add(MediaType.APPLICATION_OCTET_STREAM);
    converter.setSupportedMediaTypes(supportedMediaTypes);
  }
}