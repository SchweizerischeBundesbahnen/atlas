package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.model.api.AtlasFieldLengths;
import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
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
@ToString
@SuperBuilder
@FieldNameConstants
@Entity(name = "service_point_version_geolocation")
public class ServicePointGeolocation extends BaseEntity {

  private static final String VERSION_SEQ = "service_point_version_geolocation_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1, initialValue = 1000)
  private Long id;

  @OneToOne(mappedBy = "servicePointGeolocation")
  private ServicePointVersion servicePointVersion;

  @NotNull
  @AtlasVersionableProperty
  private Integer source_spatial_ref;

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
  private String isoCountryCode; // TODO: Country enum // ServicePoint-Country = UserEvent, Geolocation-Country=SystemEvent

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
