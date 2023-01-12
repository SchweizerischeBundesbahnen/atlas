package ch.sbb.atlas.servicepointdirectory.transformer;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
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
  private Double north;
  @NotNull
  private Double east;
  @NotNull
  private SpatialReference spatialReference;
}
