package ch.sbb.atlas.api.bodi;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
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
@ToString
@SuperBuilder
@FieldNameConstants
@Schema(name = "UpdateTransportCompanyRelation")
public class UpdateTransportCompanyRelationModel {

    @Schema(description = "id")
    private long id;

    @Schema(description = "Valid From", example = "2022-01-01")
    @NotNull
    private LocalDate validFrom;

    @Schema(description = "Valid To", example = "2022-01-01")
    @NotNull
    private LocalDate validTo;
}
