package ch.sbb.workflow.sepodi.termination.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "termination-examinant-mails")
public class TerminationExaminants {

  private static final String PROD_PROFILE = "prod";
  public static final String NON_PROD_EMAIL = "TechSupport-ATLAS@sbb.ch";

  @Value("${spring.profiles.active:local}")
  @Setter
  private String activeProfile;

  private String infoPlus;

  private String nova;

  public String getInfoPlus() {
    return PROD_PROFILE.equals(activeProfile) ? infoPlus : NON_PROD_EMAIL;
  }

  public String getNova() {
    return NON_PROD_EMAIL.equals(nova) ? nova : NON_PROD_EMAIL;
  }

}
