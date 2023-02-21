package ch.sbb.line.directory.model.search;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.searching.BusinessOrganisationDependentSearchRestriction;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.Line_;
import ch.sbb.atlas.api.lidi.enumaration.LineType;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class LineSearchRestrictions extends BusinessOrganisationDependentSearchRestriction<Line> {

  @Builder.Default
  private Optional<String> swissLineNumber = Optional.empty();

  @Singular(ignoreNullCollections = true)
  private List<LineType> typeRestrictions;

  @Override
  protected SingularAttribute<Line, Status> getStatus() {
    return Line_.status;
  }

  @Override
  public Specification<Line> getSpecification() {
    return getBaseSpecification().and(
            specificationBuilder().enumSpecification(typeRestrictions, Line_.lineType))
        .and(specificationBuilder().singleStringSpecification(
            swissLineNumber));
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
