package ch.sbb.atlas.imports.prm.relation;

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
@Schema(name = "RelationImportRequest")
public class RelationImportRequestModel {

    @Schema(name = "List of RelationCsvModelContainer to import")
    @NotNull
    @NotEmpty
    private List<RelationCsvModelContainer> relationCsvModelContainers;

}
