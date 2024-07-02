package ch.sbb.atlas.api.servicepoint;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
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
@Schema(name = "UpdateServicePointVersion")
public class UpdateDesignationOfficialServicePointModel {

    @NotNull
    @Size(min = 2, max = AtlasFieldLengths.LENGTH_30)
    @Schema(description = "Official designation of a location that must be used by all recipients"
            , example = "Biel/Bienne Bözingenfeld/Champ", maxLength = 30)
    private String designationOfficial;
}