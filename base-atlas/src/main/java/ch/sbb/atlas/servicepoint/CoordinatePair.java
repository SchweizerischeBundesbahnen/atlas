package ch.sbb.atlas.servicepoint;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CoordinatePair {

  @NotNull
  @Schema(description = "North longitude", example = "225738.00000000000")
  private Double north;

  @NotNull
  @Schema(description = "Eastern longitude", example = "681821.00000000000")
  private Double east;

  @NotNull
  private SpatialReference spatialReference;
}
