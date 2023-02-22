package ch.sbb.line.directory.model.search;

import ch.sbb.atlas.workflow.model.WorkflowStatus;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.workflow.model.BaseVersionSnapshot_;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import ch.sbb.line.directory.entity.LineVersionSnapshot.Fields;
import ch.sbb.line.directory.entity.LineVersionSnapshot_;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class LineVersionSnapshotSearchRestrictions {

  private final Pageable pageable;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;

  @Singular(ignoreNullCollections = true)
  private List<WorkflowStatus> statusRestrictions;

  @Builder.Default
  private Optional<LocalDate> validOn = Optional.empty();

  public Specification<LineVersionSnapshot> getSpecification() {
    return specificationBuilder().searchCriteriaSpecification(searchCriterias)
        .and(specificationBuilder().validOnSpecification(validOn))
        .and(getWorkflowStatusSpecification());
  }

  protected SpecificationBuilder<LineVersionSnapshot> specificationBuilder() {
    return SpecificationBuilder.<LineVersionSnapshot>builder()
        .stringAttributes(List.of(Fields.number, Fields.description, Fields.slnid))
        .validFromAttribute(LineVersionSnapshot_.validFrom)
        .validToAttribute(LineVersionSnapshot_.validTo)
        .build();
  }

  private Specification<LineVersionSnapshot> getWorkflowStatusSpecification() {
    return (root, query, criteriaBuilder) -> {
      if (statusRestrictions.isEmpty()) {
        return criteriaBuilder.and();
      }
      return root.get(BaseVersionSnapshot_.workflowStatus).in(statusRestrictions);
    };
  }

}
