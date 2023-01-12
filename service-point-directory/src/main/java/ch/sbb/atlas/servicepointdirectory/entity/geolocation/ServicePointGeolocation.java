package ch.sbb.atlas.servicepointdirectory.entity.geolocation;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.SwissCanton;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;
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

  @AtlasVersionableProperty
  @Enumerated(EnumType.STRING)
  private Country country;

  @AtlasVersionableProperty
  @Enumerated(EnumType.STRING)
  private SwissCanton swissCanton;

  @AtlasVersionableProperty
  private Integer swissDistrictNumber;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String swissDistrictName;

  @AtlasVersionableProperty
  private Integer swissMunicipalityNumber;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String swissMunicipalityName;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String swissLocalityName;

}
