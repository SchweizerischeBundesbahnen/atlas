package ch.sbb.atlas.servicepointdirectory.entity;

import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@FieldNameConstants
@Embeddable
public class LocationTypes {

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
    return (wgs84east != null && wgs84east >= -180 && wgs84east <= 180 && wgs84north != null && wgs84north >= -90 && wgs84north <= 90);
  }
}
