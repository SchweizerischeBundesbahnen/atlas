package ch.sbb.workflow.model.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.model.sepodi.WorkflowRequestParams;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@Getter
@ToString
@SuperBuilder
public class WorkflowSearchRestrictions {

    private final Pageable pageable;
    private final WorkflowRequestParams workflowRequestParams;

    @Singular(ignoreNullCollections = true)
    private List<String> searchCriterias;

    public Specification<StopPointWorkflow> getSpecification() {
        return specificationBuilder().searchCriteriaSpecification(searchCriterias)
                .and(specificationBuilder().inSpecification(workflowRequestParams.getSloids(), StopPointWorkflow.Fields.sloid))
                .and(specificationBuilder().inSpecification(workflowRequestParams.getWorkflowId(), StopPointWorkflow.Fields.id))
                .and(specificationBuilder().inSpecification(workflowRequestParams.getStatus(), StopPointWorkflow.Fields.status))
                .and(specificationBuilder().inSpecification(workflowRequestParams.getSboids(), StopPointWorkflow.Fields.sboid))
                .and(specificationBuilder().inSpecification(workflowRequestParams.getLocality(), StopPointWorkflow.Fields.swissMunicipalityName));
    }

    protected SpecificationBuilder<StopPointWorkflow> specificationBuilder() {
        return SpecificationBuilder.<StopPointWorkflow>builder()
        .stringAttributes(List.of(StopPointWorkflow.Fields.sloid))
                .build();
    }
}
