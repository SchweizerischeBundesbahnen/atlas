package ch.sbb.atlas.servicepointdirectory.entity.geolocation;

import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
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
@MappedSuperclass
public abstract class GeolocationBaseEntity extends BaseEntity {

  private static final int WGS84_EAST_MAX = 180;
  private static final int WGS84_NORTH_MAX = 90;

  @NotNull
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
  @Column(name = "e_wgs84web")
  private Double wgs84webEast;

  @AtlasVersionableProperty
  @Column(name = "n_wgs84web")
  private Double wgs84webNorth;

  @AtlasVersionableProperty
  private Double height;

  public boolean isValid() {
    return (wgs84east != null && wgs84east >= -WGS84_EAST_MAX && wgs84east <= WGS84_EAST_MAX
        && wgs84north != null && wgs84north >= -WGS84_NORTH_MAX && wgs84north <= WGS84_NORTH_MAX);
  }
}
