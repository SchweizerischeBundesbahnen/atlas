package ch.sbb.exportservice.entity.sepodi.geolocation;

import ch.sbb.exportservice.entity.sepodi.TrafficPointElementVersion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "trafficPointElementVersion")
@SuperBuilder
@FieldNameConstants
public class TrafficPointElementGeolocation extends GeolocationBaseEntity {

  private Long id;

  private TrafficPointElementVersion trafficPointElementVersion;

}
