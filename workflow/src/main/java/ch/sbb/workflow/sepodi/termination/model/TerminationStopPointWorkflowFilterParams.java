package ch.sbb.workflow.sepodi.termination.model;

import ch.sbb.workflow.sepodi.termination.entity.TerminationWorkflowStatus;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class TerminationStopPointWorkflowFilterParams {

  @Parameter(description = "List of search strings")
  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias = new ArrayList<>();

  @Parameter(description = "List of sboids")
  @Singular(ignoreNullCollections = true)
  private List<String> sboids = new ArrayList<>();

  @Parameter(description = "List of workflow ids")
  @Singular(ignoreNullCollections = true)
  private List<Integer> workflowIds = new ArrayList<>();

  @Parameter(description = "List of termination workflow status")
  @Singular(value = "status", ignoreNullCollections = true)
  private List<TerminationWorkflowStatus> status = new ArrayList<>();

}
