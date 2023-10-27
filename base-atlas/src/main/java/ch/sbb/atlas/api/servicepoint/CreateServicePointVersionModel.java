package ch.sbb.atlas.api.servicepoint;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@FieldNameConstants
@Schema(name = "CreateServicePointVersion")
public class CreateServicePointVersionModel extends UpdateServicePointVersionModel {

  // todo: check at update that country and numbershort are the same like db,
  //  check at create that user is granted for country

  @JsonIgnore
  @AssertTrue(message = "ServicePointNumber must be present only if country not in (85,11,12,13,14)")
  public boolean isValidServicePointNumber() {
    if (super.getNumberShort() == null) {
      return shouldGenerateServicePointNumber();
    } else {
      try {
        return !ServicePointConstants.AUTOMATIC_SERVICE_POINT_ID.contains(super.getCountry());
      } catch (NullPointerException exception) {
        return false;
      }
    }
  }

}
