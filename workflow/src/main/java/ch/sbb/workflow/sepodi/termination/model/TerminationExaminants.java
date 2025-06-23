package ch.sbb.workflow.sepodi.termination.model;

import ch.sbb.workflow.sepodi.BaseExaminants;
import lombok.Builder;
import lombok.Data;
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

  private InfoPlus infoPlus;

  private Nova nova;

  public String getInfoPlusMail() {
    return PROD_PROFILE.equals(activeProfile) ? infoPlus.getEmail() : NON_PROD_EMAIL;
  }

  public String getNovaMail() {
    return PROD_PROFILE.equals(activeProfile) ? nova.getEmail() : NON_PROD_EMAIL;
  }

  @Data
  @Builder
  public static class InfoPlus {

    private String email;
    private String lastname;
    private String firstname;
    private String organisation;

  }

  @Data
  @Builder
  public static class Nova {

    private String email;
    private String lastname;
    private String firstname;
    private String organisation;
  }

}
