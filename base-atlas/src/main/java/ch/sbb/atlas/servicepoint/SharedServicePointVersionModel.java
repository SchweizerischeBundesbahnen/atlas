package ch.sbb.atlas.servicepoint;

import ch.sbb.atlas.api.model.SboidsAssociated;
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
public class SharedServicePointVersionModel implements AtlasEvent, SboidsAssociated {

  private String servicePointSloid;
  private Set<String> sboids;
  private Set<String> trafficPointSloids;

}
