package ch.sbb.atlas.servicepointdirectory.transformer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CoordinatePair {

  private Double north;
  private Double east;
}
