package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.servicepoint.Country;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "GeoReference")
public class GeoReference {

  @Schema(description = "Country the location is in")
  private Country country;

  @Schema(description = "SwissCanton the location is in")
  private SwissCanton swissCanton;

  @Schema(description = "SwissDistrictNumber the location is in, based on FSO", example = "242")
  private Integer swissDistrictNumber;

  @Schema(description = "SwissDistrictName the location is in", example = "Biel/Bienne")
  private String swissDistrictName;

  @Schema(description = "SwissMunicipalityNumber the location is in, based on FSO", example = "371")
  private Integer swissMunicipalityNumber;

  @Schema(description = "SwissMunicipalityName the location is in", example = "Biel/Bienne")
  private String swissMunicipalityName;

  @Schema(description = "SwissLocalityName the location is in", example = "Biel/Bienne")
  private String swissLocalityName;

  @Schema(description = "Height of the location")
  private double height;

}
