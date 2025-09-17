package ch.sbb.atlas.api.servicepoint.sector;

import ch.sbb.atlas.api.servicepoint.GeolocationBaseCreateModel;
import ch.sbb.atlas.api.servicepoint.SpatialReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "CreateSectorVersion")
public class CreateSectorVersionModel extends SectorVersionModel {

  @NotNull
  @Valid
  private GeolocationBaseCreateModel sectorGeolocation;

  @AssertTrue(message = "Only LV95 and WGS84 are allowed")
  @JsonIgnore
  public boolean isSpatialReferenceAllowed() {
    return getSectorGeolocation().getSpatialReference() == SpatialReference.LV95
        || getSectorGeolocation().getSpatialReference() == SpatialReference.WGS84;
  }

}
