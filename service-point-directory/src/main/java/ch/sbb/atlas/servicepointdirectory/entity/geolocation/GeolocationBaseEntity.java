package ch.sbb.atlas.servicepointdirectory.entity.geolocation;

import ch.sbb.atlas.api.servicepoint.TransformableGeolocation;
import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepointdirectory.entity.BaseDidokImportEntity;
import ch.sbb.atlas.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.versioning.annotation.AtlasVersionableProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
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
public abstract class GeolocationBaseEntity extends BaseDidokImportEntity implements TransformableGeolocation {

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private SpatialReference spatialReference;

  @AtlasVersionableProperty
  @NotNull
  @Digits(integer = 19, fraction = 11)
  private Double east;

  @AtlasVersionableProperty
  @NotNull
  @Digits(integer = 19, fraction = 11)
  private Double north;

  @AtlasVersionableProperty
  @Digits(integer = 10, fraction = 4)
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
