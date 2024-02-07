package ch.sbb.atlas.imports.prm.contactpoint;

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
@Schema(name = "ContactPointImportRequest")
public class ContactPointImportRequestModel {

    @Schema(name = "List of ContactPointCsvModelContainer to import")
    @NotNull
    @NotEmpty
    private List<ContactPointCsvModelContainer> contactPointCsvModelContainers;
}
