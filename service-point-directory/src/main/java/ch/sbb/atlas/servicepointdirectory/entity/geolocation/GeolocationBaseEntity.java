package ch.sbb.atlas.servicepointdirectory.entity.geolocation;

import ch.sbb.atlas.base.service.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionable;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.servicepointdirectory.entity.BaseDidokImportEntity;
import ch.sbb.atlas.servicepointdirectory.model.CoordinatePair;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
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
public abstract class GeolocationBaseEntity extends BaseDidokImportEntity {

  @NotNull
  @Enumerated(EnumType.STRING)
  @AtlasVersionableProperty
  private SpatialReference spatialReference;

  @AtlasVersionableProperty
  @NotNull
  private Double east;

  @AtlasVersionableProperty
  @NotNull
  private Double north;

  @AtlasVersionableProperty
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
