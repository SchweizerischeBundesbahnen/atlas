package ch.sbb.importservice.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import ch.sbb.importservice.model.BusinessObjectType;
import ch.sbb.importservice.model.ImportType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@SuperBuilder
@FieldNameConstants
@Schema(name = "BulkImportRequest")
public class BulkImportRequest {

    @NotNull
    private ApplicationType applicationType;

    @NotNull
    private BusinessObjectType objectType;

    @NotNull
    private ImportType importType;

    private String inNameOf;

    private List<String> emails;
}
