package ch.sbb.exportservice.job.sepodi.servicepoint.entity;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.exportservice.job.sepodi.GeolocationBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@FieldNameConstants
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
