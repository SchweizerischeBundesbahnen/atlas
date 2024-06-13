package ch.sbb.workflow.model.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.workflow.entity.StopPointWorkflow;
import ch.sbb.workflow.model.sepodi.StopPointWorkflowRequestParams;
import ch.sbb.workflow.specification.ValidFromAndCreatedAtSpecification;
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
public class StopPointWorkflowSearchRestrictions {

    private final Pageable pageable;
    private final StopPointWorkflowRequestParams stopPointWorkflowRequestParams;

    @Singular(ignoreNullCollections = true)
    private List<String> searchCriterias;

    public Specification<StopPointWorkflow> getSpecification() {
        return specificationBuilder().searchCriteriaSpecification(searchCriterias)
                .and(specificationBuilder().inSpecification(stopPointWorkflowRequestParams.getSloids(), StopPointWorkflow.Fields.sloid))
                .and(specificationBuilder().inSpecification(stopPointWorkflowRequestParams.getWorkflowIds(), StopPointWorkflow.Fields.id))
                .and(specificationBuilder().inSpecification(stopPointWorkflowRequestParams.getStatus(), StopPointWorkflow.Fields.status))
                .and(specificationBuilder().inSpecification(stopPointWorkflowRequestParams.getSboids(), StopPointWorkflow.Fields.sboid))
                .and(specificationBuilder().inSpecification(stopPointWorkflowRequestParams.getLocalityName(), StopPointWorkflow.Fields.localityName))
                .and(specificationBuilder().inSpecification(stopPointWorkflowRequestParams.getDesignationOfficial(), StopPointWorkflow.Fields.designationOfficial))
                .and(new ValidFromAndCreatedAtSpecification<>(
                        stopPointWorkflowRequestParams.getVersionValidFrom(),
                        stopPointWorkflowRequestParams.getCreatedAt()
                ));
    }

    protected SpecificationBuilder<StopPointWorkflow> specificationBuilder() {
        return SpecificationBuilder.<StopPointWorkflow>builder()
                .stringAttributes(
                        List.of(StopPointWorkflow.Fields.sloid,
                                StopPointWorkflow.Fields.designationOfficial))
                .build();
    }
}
