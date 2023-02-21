package ch.sbb.atlas.servicepointdirectory.entity.geolocation;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.SwissCanton;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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

  @OneToOne(mappedBy = "servicePointGeolocation")
  private ServicePointVersion servicePointVersion;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private Country country;

  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private SwissCanton swissCanton;

  @AtlasVersionableProperty
  private Integer swissDistrictNumber;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String swissDistrictName;

  @AtlasVersionableProperty
  private Integer swissMunicipalityNumber;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String swissMunicipalityName;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @AtlasVersionableProperty
  private String swissLocalityName;

}
