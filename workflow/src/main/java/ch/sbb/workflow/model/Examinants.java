package ch.sbb.workflow.model;

import ch.sbb.atlas.api.workflow.ClientPersonModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import java.util.List;
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
@ConfigurationProperties(prefix = "examinants")
public class Examinants {

  private SpecialistOffice specialistOffice;

  private List<Canton> cantons;

  public ClientPersonModel getExaminantPersonByCanton(SwissCanton canton) {
    Canton examinantByCanton = getExaminantByCanton(SwissCanton.fromAbbreviation(canton.getAbbreviation()));
    return ClientPersonModel.builder()
        .personFunction(examinantByCanton.getFunction())
        .firstName(examinantByCanton.getFirstname())
        .lastName(examinantByCanton.getLastname())
        .mail(examinantByCanton.getEmail())
        .build();
  }

  public ClientPersonModel getExaminantSpecialistOffice() {
    return ClientPersonModel.builder()
        .personFunction(specialistOffice.getFunction())
        .firstName(specialistOffice.getFirstname())
        .lastName(specialistOffice.getLastname())
        .mail(specialistOffice.getEmail())
        .build();
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
