package ch.sbb.atlas.servicepointdirectory.transformer;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
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

  private Double north;
  private Double east;
  private SpatialReference spatialReference;
}
