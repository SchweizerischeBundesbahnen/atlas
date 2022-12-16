package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.servicepointdirectory.enumeration.Country;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotNull;
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
@ToString(exclude = "trafficPointElementVersion")
@SuperBuilder
@FieldNameConstants
@Entity(name = "traffic_point_element_version_geolocation")
public class TrafficPointElementGeolocation extends BaseEntity {

  private static final String VERSION_SEQ = "traffic_point_element_version_geolocation_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @OneToOne(mappedBy = "trafficPointElementGeolocation")
  private TrafficPointElementVersion trafficPointElementVersion;

  @Embedded
  private LocationTypes locationTypes;

  @AtlasVersionableProperty
  @Enumerated(EnumType.STRING)
  private Country country;

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
