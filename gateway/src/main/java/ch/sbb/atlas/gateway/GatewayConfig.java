package ch.sbb.atlas.gateway;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gateway")
@Getter
@Setter
public class GatewayConfig {

  private Map<String, String> routes;

}