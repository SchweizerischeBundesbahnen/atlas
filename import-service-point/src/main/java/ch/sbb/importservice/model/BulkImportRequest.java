package ch.sbb.importservice.model;

import ch.sbb.atlas.kafka.model.user.admin.ApplicationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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

  private List<@Email @NotEmpty String> emails;

}
