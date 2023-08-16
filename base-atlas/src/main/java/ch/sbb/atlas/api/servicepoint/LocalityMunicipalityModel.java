package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "LocalityMunicipality")
public class LocalityMunicipalityModel {

  @Schema(description = "SwissMunicipalityNumber the location is in, based on FSO", example = "371")
  private Integer fsoNumber;

  @Schema(description = "SwissMunicipalityName the location is in", example = "Biel/Bienne")
  @Size(max = AtlasFieldLengths.LENGTH_255)
  private String municipalityName;

  @Size(max = AtlasFieldLengths.LENGTH_255)
  @Schema(description = "SwissLocalityName the location is in", example = "Biel/Bienne")
  private String localityName;

}