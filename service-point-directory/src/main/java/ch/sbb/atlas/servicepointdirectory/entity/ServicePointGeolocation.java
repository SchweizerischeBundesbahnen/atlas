package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "servicePointVersion")
@SuperBuilder
@FieldNameConstants
@Entity(name = "service_point_version_geolocation")
public class ServicePointGeolocation extends BaseEntity {

  private static final String VERSION_SEQ = "service_point_version_geolocation_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1,
      initialValue = 1000)
  private Long id;

  @OneToOne(mappedBy = "servicePointGeolocation")
  private ServicePointVersion servicePointVersion;

  @Embedded
  private LocationTypes locationTypes;

  @AtlasVersionableProperty
  @Enumerated(EnumType.STRING)
  private Country country; // ServicePoint-Country = UserEvent, Geolocation-Country=SystemEvent

  @AtlasVersionableProperty
  private Integer swissCantonFsoNumber;

  @AtlasVersionableProperty
  private Integer swissCantonNumber;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_50)
  private String swissCantonName;

  @AtlasVersionableProperty
  private Integer swissDistrictNumber;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String swissDistrictName;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String swissMunicipalityName;

  @AtlasVersionableProperty
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String swissLocalityName;

  public boolean isValid() {
    return locationTypes.isValid();
  }
}
