package ch.sbb.workflow.sepodi.termination.model;

import ch.sbb.workflow.sepodi.BaseExaminants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "termination-examinant-mails")
public class TerminationExaminants extends BaseExaminants {

  public static final String NON_PROD_EMAIL = "TechSupport-ATLAS@sbb.ch";

  private String infoPlus;

  private String nova;

  public String getInfoPlus() {
    return PROD_PROFILE.equals(activeProfile) ? infoPlus : NON_PROD_EMAIL;
  }

  public String getNova() {
    return PROD_PROFILE.equals(activeProfile) ? nova : NON_PROD_EMAIL;
  }

}
