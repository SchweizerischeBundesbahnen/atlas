package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.servicepoint.Country;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "ServicePointGeolocationCreate")
public class ServicePointGeolocationCreateModel extends GeolocationBaseCreateModel implements TransformableGeolocation {

  private Country country;
  private SwissLocation swissLocation;

}