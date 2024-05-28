package ch.sbb.workflow.model.sepodi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Schema(name = "ReadStopPointWorkflow")
public class ReadStopPointWorkflowModel extends BaseStopPointWorkflowModel{

}
