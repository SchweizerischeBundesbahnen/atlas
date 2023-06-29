package ch.sbb.exportservice.entity.geolocation;

import ch.sbb.exportservice.entity.LoadingPointVersion;
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
public class LoadingPointGeolocation extends GeolocationBaseEntity {

  private Long id;

  private LoadingPointVersion loadingPointVersion;

}
