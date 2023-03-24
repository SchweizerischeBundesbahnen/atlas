package ch.sbb.atlas.user.administration.security;

import ch.sbb.atlas.api.model.BusinessOrganisationAssociated;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
public class BusinessObject implements BusinessOrganisationAssociated {

  private String anotherValue;
  private String businessOrganisation;
  private LocalDate validFrom;
  private LocalDate validTo;

  public static BusinessObjectBuilder createDummy() {
    return BusinessObject.builder()
                         .anotherValue("value")
                         .businessOrganisation("sboid")
                         .validFrom(LocalDate.of(2020, 1, 1))
                         .validTo(LocalDate.of(2020, 12, 31));
  }

}
