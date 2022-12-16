package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import javax.persistence.Column;
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
@ToString
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

  @NotNull
  @AtlasVersionableProperty
  @Enumerated(EnumType.STRING)
  private SpatialReference spatialReference;

  @AtlasVersionableProperty
  @Column(name = "e_lv03")
  private Double lv03east;

  @AtlasVersionableProperty
  @Column(name = "n_lv03")
  private Double lv03north;

  @AtlasVersionableProperty
  @Column(name = "e_lv95")
  private Double lv95east;

  @AtlasVersionableProperty
  @Column(name = "n_lv95")
  private Double lv95north;

  @AtlasVersionableProperty
  @Column(name = "e_wgs84")
  private Double wgs84east;

  @AtlasVersionableProperty
  @Column(name = "n_wgs84")
  private Double wgs84north;

  @AtlasVersionableProperty
  private Double height;

  @AtlasVersionableProperty
  @Size(max = 2)
  private String isoCountryCode;

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
}
