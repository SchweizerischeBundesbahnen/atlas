package ch.sbb.atlas.useradministration.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "azure-config")
@Getter
@Setter
public class AzureConfig {

  private String tenantId;
  private String clientId;
  private String clientSecret;

}
