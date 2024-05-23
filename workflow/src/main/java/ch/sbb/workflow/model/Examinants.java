package ch.sbb.workflow.model;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.workflow.entity.Person;
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

  private List<Canton> cantons;

  public Canton getExaminantByCanton(SwissCanton canton) {
    return cantons.stream().filter(examinant -> canton.getAbbreviation().equals(examinant.canton)).findFirst()
        .orElseThrow(() -> new IllegalStateException("Canton abbreviation does not exists"));
  }

  public Person getExaminantPersonByCanton(String canton) {
    Canton examinantByCanton = getExaminantByCanton(SwissCanton.fromAbbreviation(canton));
    return Person.builder()
        .function(examinantByCanton.getFunction())
        .firstName(examinantByCanton.getFirstname())
        .lastName(examinantByCanton.getLastname())
        .mail(examinantByCanton.getEmail())
        .build();
  }

  @Data
  @Builder
  public static class Canton {

    private String canton;
    private String lastname;
    private String firstname;
    private String organisation;
    private String email;
    private String function;
  }

}
