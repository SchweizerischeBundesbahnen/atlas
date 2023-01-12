package ch.sbb.atlas.servicepointdirectory.entity.geolocation;

import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
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
@Entity(name = "traffic_point_element_version_geolocation")
public class TrafficPointElementGeolocation extends GeolocationBaseEntity {

  private static final String VERSION_SEQ = "traffic_point_element_version_geolocation_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1,
      initialValue = 1000)
  private Long id;

  @OneToOne(mappedBy = "trafficPointElementGeolocation")
  private TrafficPointElementVersion trafficPointElementVersion;

}
