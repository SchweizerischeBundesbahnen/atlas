package ch.sbb.atlas.kafka.model.service.point;

import ch.sbb.atlas.kafka.model.AtlasEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class SharedServicePointVersionModel implements AtlasEvent {

  private String servicePointSloid;
  private Set<String> sboids;
  private Set<String> trafficPointSloids;
  private boolean isStopPoint;

}
