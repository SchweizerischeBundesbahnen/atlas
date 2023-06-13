package ch.sbb.exportservice.entity.geolocation;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.Size;
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
@Entity(name = "service_point_version_geolocation")
public class ServicePointGeolocation extends GeolocationBaseEntity {

  private static final String VERSION_SEQ = "service_point_version_geolocation_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1,
      initialValue = 1000)
  private Long id;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private Country country;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  @Column(name = "swiss_canton")
  private SwissCanton swissCanton;

  @AtlasVersionableProperty
  @Column(name = "swiss_district_number")
  private Integer swissDistrictNumber;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  @Column(name = "swiss_district_name")
  private String swissDistrictName;

  @AtlasVersionableProperty
  @Column(name = "swiss_municipality_number")
  private Integer swissMunicipalityNumber;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  @Column(name = "swiss_municipality_name")
  private String swissMunicipalityName;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  @Column(name = "swiss_locality_name")
  private String swissLocalityName;

}
