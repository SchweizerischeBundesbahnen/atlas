package ch.sbb.line.directory.model;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.searching.SearchRestrictions;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.Subline_;
import ch.sbb.line.directory.enumaration.SublineType;
import java.util.List;
import javax.persistence.metamodel.SingularAttribute;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class SublineSearchRestrictions extends SearchRestrictions<Subline> {

  @Singular(ignoreNullCollections = true)
  private List<SublineType> typeRestrictions;

  @Override
  protected SingularAttribute<Subline, Status> getStatus() {
    return Subline_.status;
  }

  @Override
  public Specification<Subline> getSpecification(
      SpecificationBuilder<Subline> specificationBuilder) {
    return getBaseSpecification(specificationBuilder).and(
        specificationBuilder.enumSpecification(typeRestrictions, Subline_.sublineType));
  }
}
