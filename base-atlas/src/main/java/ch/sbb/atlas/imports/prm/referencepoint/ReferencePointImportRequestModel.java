package ch.sbb.atlas.imports.prm.referencepoint;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(name = "ReferencePointImportRequest")
public class ReferencePointImportRequestModel {

    @Schema(name = "List of ReferencePointCsvModelContainer to import")
    @NotNull
    @NotEmpty
    private List<ReferencePointCsvModelContainer> referencePointCsvModelContainers;

}
