package ch.sbb.exportservice.job.sepodi.trafficpoint;

import ch.sbb.exportservice.job.sepodi.GeolocationBaseEntity;
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
