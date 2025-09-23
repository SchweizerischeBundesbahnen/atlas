package ch.sbb.line.directory.module.line.search;

import ch.sbb.atlas.api.lidi.LineRequestParams;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.searching.SearchRestrictions;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.SingleStringSpecification;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.line.directory.module.line.entity.Line;
import ch.sbb.line.directory.module.line.entity.Line.Fields;
import ch.sbb.line.directory.module.line.entity.Line_;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class LineSearchRestrictions extends SearchRestrictions<Line> {

  private final Pageable pageable;
  private final LineRequestParams lineRequestParams;

  @Override
  protected SingularAttribute<Line, Status> getStatus() {
    return Line_.status;
  }

  @Override
  public List<String> getSearchCriterias() {
    return lineRequestParams.getSearchCriteria();
  }

  @Override
  public List<Status> getStatusRestrictions() {
    return lineRequestParams.getStatusRestrictions();
  }

  @Override
  public Specification<Line> getSpecification() {
    return specificationBuilder().searchCriteriaSpecification(lineRequestParams.getSearchCriteria())
        .and(specificationBuilder().validOnSpecification(lineRequestParams.getValidOn()))
        .and(specificationBuilder().enumSpecification(getStatusRestrictions(), getStatus()))
        .and(specificationBuilder().enumSpecification(lineRequestParams.getTypeRestrictions(), Line_.lidiElementType))
        .and(specificationBuilder().enumSpecification(lineRequestParams.getElementRestrictions(), Line_.elementType))
        .and(new SingleStringSpecification<>(lineRequestParams.getSwissLineNumber(), Fields.swissLineNumber))
        .and(new SingleStringSpecification<>(lineRequestParams.getBusinessOrganisation(), Fields.businessOrganisation))
        .and(new ValidOrEditionTimerangeSpecification<>(
                lineRequestParams.getFromDate(),
                lineRequestParams.getToDate(),
                lineRequestParams.getValidToFromDate(),
                null,
                null
            )
        );
  }

  @Override
  protected SpecificationBuilder<Line> specificationBuilder() {
    return SpecificationBuilder.<Line>builder()
        .stringAttributes(
            List.of(Line.Fields.swissLineNumber,
                Line.Fields.number,
                Line.Fields.description,
                Line.Fields.slnid))
        .validFromAttribute(Line_.validFrom)
        .validToAttribute(Line_.validTo)
        .singleStringAttribute(Line_.swissLineNumber)
        .build();
  }
}
