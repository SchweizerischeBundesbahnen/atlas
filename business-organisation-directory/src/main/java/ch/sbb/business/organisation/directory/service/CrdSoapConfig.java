package ch.sbb.business.organisation.directory.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "external.crd")
@Getter
@Setter
public class CrdSoapConfig {

  private String url;
  private String username;
  private String password;
  private Resource keystore;
  private String keystorePassword;
  private String keystoreAlias;
  private Resource truststore;
  private String truststorePassword;

}