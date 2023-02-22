package ch.sbb.atlas.servicepointdirectory.model;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
  @Schema(description = "North longitude", example = "47.15838")
  private Double north;

  @NotNull
  @Schema(description = "Eastern longitude", example = "7.29464")
  private Double east;

  @NotNull
  @JsonIgnore
  private SpatialReference spatialReference;
}
