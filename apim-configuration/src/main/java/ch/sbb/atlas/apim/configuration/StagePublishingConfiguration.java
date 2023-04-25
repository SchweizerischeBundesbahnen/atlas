package ch.sbb.atlas.apim.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties("publishing")
class StagePublishingConfiguration {

  private Map<String, StageConfig> stages;

  @Getter
  @Setter
  public static class StageConfig {

    private String exportDirectory;
    private List<String> apis = new ArrayList<>();
    private List<String> excludePatterns = new ArrayList<>();

    public boolean isNotExcluded(String key) {
      return getExcludePatterns().stream().noneMatch(key::contains);
    }
  }

}