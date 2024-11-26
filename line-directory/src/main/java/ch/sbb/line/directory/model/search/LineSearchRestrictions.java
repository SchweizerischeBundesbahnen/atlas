package ch.sbb.line.directory.model.search;

import ch.sbb.atlas.api.lidi.LineRequestParams;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.searching.SearchRestrictions;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.Line_;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Optional;
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
        .and(specificationBuilder().validOnSpecification(Optional.ofNullable(lineRequestParams.getValidOn())))
        .and(specificationBuilder().enumSpecification(getStatusRestrictions(), getStatus()))
        .and(specificationBuilder().enumSpecification(lineRequestParams.getTypeRestrictions(), Line_.lidiElementType))
        .and(specificationBuilder().singleStringSpecification(Optional.ofNullable(lineRequestParams.getSwissLineNumber())))
        .and(specificationBuilder().singleStringSpecification(Optional.ofNullable(lineRequestParams.getBusinessOrganisation())))
        .and(new ValidOrEditionTimerangeSpecification<>(
            lineRequestParams.getFromDate(),
            lineRequestParams.getToDate(),
            lineRequestParams.getValidToFromDate(),
            lineRequestParams.getCreatedAfter(),
            lineRequestParams.getModifiedAfter()));
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
