package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.kafka.model.SwissCanton;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@Schema(name = "SwissLocation")
public class SwissLocation {

  @Schema(description = "SwissCanton the location is in")
  private SwissCanton canton;

  @Schema(accessMode = AccessMode.READ_ONLY)
  private Canton cantonInformation;

  private DistrictModel district;

  private LocalityMunicipalityModel localityMunicipality;

}