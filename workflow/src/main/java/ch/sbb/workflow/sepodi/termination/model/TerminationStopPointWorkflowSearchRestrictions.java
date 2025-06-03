package ch.sbb.workflow.sepodi.termination.model;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.workflow.sepodi.termination.entity.TerminationStopPointWorkflow;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class TerminationStopPointWorkflowSearchRestrictions {

  private final Pageable pageable;
  private final TerminationStopPointWorkflowFilterParams filterParams;

  public Specification<TerminationStopPointWorkflow> getSpecification() {
    return specificationBuilder().searchCriteriaSpecification(filterParams.getSearchCriterias())
        .and(specificationBuilder().inSpecification(filterParams.getWorkflowIds(),
            TerminationStopPointWorkflow.Fields.id))
        .and(specificationBuilder().inSpecification(filterParams.getStatus(),
            TerminationStopPointWorkflow.Fields.status))
        .and(specificationBuilder().inSpecification(filterParams.getSboids(),
            TerminationStopPointWorkflow.Fields.sboid));
  }

  protected SpecificationBuilder<TerminationStopPointWorkflow> specificationBuilder() {
    return SpecificationBuilder.<TerminationStopPointWorkflow>builder()
        .stringAttributes(
            List.of(TerminationStopPointWorkflow.Fields.sloid,
                TerminationStopPointWorkflow.Fields.designationOfficial))
        .build();
  }

}
