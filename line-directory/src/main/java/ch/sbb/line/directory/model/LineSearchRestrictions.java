package ch.sbb.line.directory.model;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.searching.SearchRestrictions;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.Line_;
import ch.sbb.line.directory.enumaration.LineType;
import java.util.List;
import java.util.Optional;
import javax.persistence.metamodel.SingularAttribute;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class LineSearchRestrictions extends SearchRestrictions<Line> {

  @Builder.Default
  private Optional<String> swissLineNumber = Optional.empty();

  @Singular(ignoreNullCollections = true)
  private List<LineType> typeRestrictions;


  @Override
  protected SingularAttribute<Line, Status> getStatus() {
    return Line_.status;
  }

  @Override
  public Specification<Line> getSpecification(SpecificationBuilder<Line> specificationBuilder) {
    return getBaseSpecification(specificationBuilder).and(
                                                         specificationBuilder.enumSpecification(typeRestrictions, Line_.lineType))
                                                     .and(
                                                         specificationBuilder.singleStringSpecification(
                                                             swissLineNumber));
  }
}
