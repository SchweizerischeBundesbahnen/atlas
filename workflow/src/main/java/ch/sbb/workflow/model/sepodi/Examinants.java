package ch.sbb.workflow.model.sepodi;

import ch.sbb.atlas.kafka.model.SwissCanton;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
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
@ConfigurationProperties(prefix = "examinants")
public class Examinants {

  private static String PROD_PROFILE = "prod";
  private static String NON_PROD_EMAIL = "TechSupport-ATLAS@sbb.ch";

  @Value("${spring.profiles.active:local}")
  @Setter
  private String activeProfile;

  private SpecialistOffice specialistOffice;

  private List<Canton> cantons;

  public List<StopPointClientPersonModel> getExaminants(SwissCanton swissCanton) {
    StopPointClientPersonModel examinantPersonByCanton = getExaminantPersonByCanton(swissCanton);
    StopPointClientPersonModel examinantSpecialistOffice = getExaminantSpecialistOffice();
    List<StopPointClientPersonModel> personModels = new ArrayList<>();
    personModels.add(examinantSpecialistOffice);
    personModels.add(examinantPersonByCanton);
    return personModels;
  }

  StopPointClientPersonModel getExaminantPersonByCanton(SwissCanton canton) {
    Canton examinantByCanton = getExaminantByCanton(SwissCanton.fromAbbreviation(canton.getAbbreviation()));
    return StopPointClientPersonModel.builder()
        .personFunction(examinantByCanton.getFunction())
        .firstName(examinantByCanton.getFirstname())
        .lastName(examinantByCanton.getLastname())
        .mail(getEmailByEnv(examinantByCanton.getEmail()))
        .organisation(examinantByCanton.getOrganisation())
        .build();
  }

  StopPointClientPersonModel getExaminantSpecialistOffice() {
    return StopPointClientPersonModel.builder()
        .personFunction(specialistOffice.getFunction())
        .firstName(specialistOffice.getFirstname())
        .lastName(specialistOffice.getLastname())
        .organisation(specialistOffice.getOrganisation())
        .mail(specialistOffice.getEmail())
        .build();
  }

  private String getEmailByEnv(String mail) {
    return PROD_PROFILE.equals(activeProfile) ? mail : NON_PROD_EMAIL;
  }

  Canton getExaminantByCanton(SwissCanton canton) {
    return cantons.stream().filter(examinant -> canton.getAbbreviation().equals(examinant.abbreviation)).findFirst()
        .orElseThrow(() -> new IllegalStateException("Canton abbreviation does not exists"));
  }

  @Data
  @Builder
  private static class Canton {

    private String abbreviation;
    private String lastname;
    private String firstname;
    private String organisation;
    private String email;
    private String function;
  }

  @Data
  @Builder
  private static class SpecialistOffice {
    private String lastname;
    private String firstname;
    private String organisation;
    private String email;
    private String function;
  }

}
