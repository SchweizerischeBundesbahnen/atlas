package ch.sbb.atlas.user.administration.security.service;

import ch.sbb.atlas.api.model.CantonAssociated;
import ch.sbb.atlas.kafka.model.SwissCanton;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class CantonObject implements CantonAssociated {

  private String anotherValue;
  private SwissCanton swissCanton;
  private LocalDate validFrom;
  private LocalDate validTo;

  public static CantonObject createDummy() {
    return CantonObject.builder()
        .anotherValue("value")
        .swissCanton(SwissCanton.BERN).build();
  }

}
