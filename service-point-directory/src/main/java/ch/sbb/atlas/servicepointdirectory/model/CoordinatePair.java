package ch.sbb.atlas.servicepointdirectory.model;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
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
  @Schema(description = "North longitude")
  private Double north;

  @NotNull
  @Schema(description = "Eastern longitude")
  private Double east;

  @NotNull
  @JsonIgnore
  private SpatialReference spatialReference;
}
