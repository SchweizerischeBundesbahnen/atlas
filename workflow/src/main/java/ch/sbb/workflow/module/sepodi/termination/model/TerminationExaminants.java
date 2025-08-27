package ch.sbb.workflow.module.sepodi.termination.model;

import ch.sbb.workflow.module.sepodi.BaseExaminants;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Setter
@Configuration
@ConfigurationProperties(prefix = "termination-examinant-mails")
public class TerminationExaminants extends BaseExaminants {

  public static final String NON_PROD_EMAIL = "TechSupport-ATLAS@sbb.ch";

  private InfoPlus infoPlus;

  private Nova nova;

  public InfoPlus getInfoPlus() {
    if (infoPlus != null) {
      infoPlus.setEmail(PROD_PROFILE.equals(activeProfile) ? infoPlus.getEmail() : NON_PROD_EMAIL);
    }
    return infoPlus;
  }

  public Nova getNova() {
    if (nova != null) {
      nova.setEmail(PROD_PROFILE.equals(activeProfile) ? nova.getEmail() : NON_PROD_EMAIL);
    }
    return nova;
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
