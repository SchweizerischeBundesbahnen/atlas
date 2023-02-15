package ch.sbb.atlas.servicepointdirectory.entity.geolocation;

import ch.sbb.atlas.servicepointdirectory.entity.LoadingPointVersion;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
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
@ToString(exclude = "loadingPointVersion")
@SuperBuilder
@FieldNameConstants
@Entity(name = "loading_point_version_geolocation")
public class LoadingPointGeolocation extends GeolocationBaseEntity {

  private static final String VERSION_SEQ = "loading_point_version_geolocation_seq";

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = VERSION_SEQ)
  @SequenceGenerator(name = VERSION_SEQ, sequenceName = VERSION_SEQ, allocationSize = 1,
      initialValue = 1000)
  private Long id;

  @OneToOne(mappedBy = "loadingPointGeolocation")
  private LoadingPointVersion loadingPointVersion;

}
