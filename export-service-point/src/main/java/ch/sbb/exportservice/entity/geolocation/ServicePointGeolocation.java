package ch.sbb.exportservice.entity.geolocation;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "servicePointVersion")
@SuperBuilder
@FieldNameConstants
@AtlasVersionable
public class ServicePointGeolocation extends GeolocationBaseEntity {

  private Long id;

  private Country country;

  private SwissCanton swissCanton;

  private Integer swissDistrictNumber;

  private String swissDistrictName;

  private Integer swissMunicipalityNumber;

  private String swissMunicipalityName;

  private String swissLocalityName;

}
