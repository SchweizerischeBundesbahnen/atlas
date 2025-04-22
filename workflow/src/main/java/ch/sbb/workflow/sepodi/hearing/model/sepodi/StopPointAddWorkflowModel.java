package ch.sbb.workflow.sepodi.hearing.model.sepodi;

import ch.sbb.atlas.api.AtlasFieldLengths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Schema(name = "StopPointAddWorkflow")
public class StopPointAddWorkflowModel extends BaseStopPointWorkflowModel {

  @Size(min = 1, max = AtlasFieldLengths.LENGTH_255)
  @NotEmpty
  @Schema(description = "Applicant mail", example = "me@you.ch")
  private String applicantMail;

}
