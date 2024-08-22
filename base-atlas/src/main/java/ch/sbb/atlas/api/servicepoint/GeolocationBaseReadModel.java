package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.servicepoint.CoordinatePair;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "Geolocation")
public class GeolocationBaseReadModel {

  @Schema(description = "Coordinate system spatial reference", example = "LV95")
  @NotNull
  private SpatialReference spatialReference;

  @NotNull
  private CoordinatePair lv95;

  @NotNull
  private CoordinatePair wgs84;

  @NotNull
  private CoordinatePair lv03;

  @Schema(description = "Height of the coordinate point", example = "540.20000")
  private Double height;

}
