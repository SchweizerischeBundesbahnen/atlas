package ch.sbb.atlas.servicepointdirectory.service.georeference;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.Country;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoReference {

  private Country country;
  private SwissCanton swissCanton;
  private Integer swissDistrictNumber;
  private String swissDistrictName;
  private Integer swissMunicipalityNumber;
  private String swissMunicipalityName;
  private String swissLocalityName;

}
