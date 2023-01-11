package ch.sbb.atlas.servicepointdirectory.entity.geolocation;

import ch.sbb.atlas.base.service.model.entity.BaseEntity;
import ch.sbb.atlas.base.service.versioning.annotation.AtlasVersionableProperty;
import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
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

  @NotNull
  @Enumerated(EnumType.STRING)
  private SpatialReference spatialReference;

  @AtlasVersionableProperty
  @NotNull
  private Double east;

  @AtlasVersionableProperty
  @NotNull
  private Double north;

  @AtlasVersionableProperty
  private Double height;

  public boolean isValid() {
    return (east != null && north != null);
  }
}
