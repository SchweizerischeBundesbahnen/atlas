package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
@Schema(name = "ReadTrafficPointElementVersion")
public class ReadTrafficPointElementVersionModel extends TrafficPointElementVersionModel {

  @NotNull
  @Valid
  private ServicePointNumber servicePointNumber;

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_500)
  @Schema(description = "Unique code for locations that is used in customer information. The structure is described in the "
      + "“Swiss Location ID” specification, chapter 4.2. The document is available here. "
      + "https://transportdatamanagement.ch/standards/", example = "ch:1:sloid:18771")
  private String servicePointSloid;

  public String getServicePointSloid() {
    return ServicePointNumber.calculateSloid(this.servicePointNumber);
  }

  private GeolocationBaseReadModel trafficPointElementGeolocation;

  @JsonInclude
  @Schema(description = "TrafficPointElementVersion has a Geolocation")
  public boolean isHasGeolocation() {
    return trafficPointElementGeolocation != null;
  }
  
}
