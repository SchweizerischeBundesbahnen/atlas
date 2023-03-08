package ch.sbb.atlas.apim.configuration;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties("production")
class ProductionConfiguration {

  private List<String> apis;
  private List<String> excludePatterns;

}