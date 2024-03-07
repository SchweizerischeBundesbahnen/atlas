package ch.sbb.exportservice.entity.sepodi.geolocation;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.Country;
import java.time.LocalDate;
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
@ToString
@SuperBuilder
@FieldNameConstants
public class ServicePointGeoData extends GeolocationBaseEntity {

  private Long id;
  private String sloid;
  private Integer number;
  private Country country;
  private SwissCanton swissCanton;
  private Integer swissDistrictNumber;
  private String swissDistrictName;
  private Integer swissMunicipalityNumber;
  private String swissMunicipalityName;
  private String swissLocalityName;
  private LocalDate validFrom;
  private LocalDate validTo;
}
