package ch.sbb.exportservice.entity.geolocation;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.exportservice.entity.BaseEntity;
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
public abstract class GeolocationBaseEntity extends BaseEntity {

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

}
