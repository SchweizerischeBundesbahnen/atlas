package ch.sbb.exportservice.entity.geolocation;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.exportservice.entity.BaseDidokImportEntity;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
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
@AtlasVersionable
public abstract class GeolocationBaseEntity extends BaseDidokImportEntity {

  @Column(name = "spatial_reference")
  private SpatialReference spatialReference;

  private Double east;

  private Double north;

  private Double height;

  public CoordinatePair asCoordinatePair() {
    return CoordinatePair.builder()
        .east(east)
        .north(north)
        .spatialReference(spatialReference)
        .build();
  }

  public boolean isValid() {
    return (east != null && north != null);
  }
}
