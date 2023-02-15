package ch.sbb.line.directory.model.search;

import ch.sbb.atlas.base.service.model.Status;
import ch.sbb.atlas.searching.BusinessOrganisationDependentSearchRestriction;
import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.Subline_;
import ch.sbb.atlas.api.lidi.enumaration.SublineType;
import java.util.List;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class SublineSearchRestrictions extends
    BusinessOrganisationDependentSearchRestriction<Subline> {

  @Singular(ignoreNullCollections = true)
  private List<SublineType> typeRestrictions;

  @Override
  protected SingularAttribute<Subline, Status> getStatus() {
    return Subline_.status;
  }

  @Override
  public Specification<Subline> getSpecification() {
    return getBaseSpecification().and(
        specificationBuilder().enumSpecification(typeRestrictions, Subline_.sublineType));
  }

  @Override
  protected SpecificationBuilder<Subline> specificationBuilder() {
    return SpecificationBuilder.<Subline>builder()
        .stringAttributes(
            List.of(Subline.Fields.swissSublineNumber,
                Subline.Fields.description,
                Subline.Fields.swissLineNumber,
                Subline.Fields.slnid,
                Subline.Fields.number))
        .validFromAttribute(Subline_.validFrom)
        .validToAttribute(Subline_.validTo)
        .build();
  }
}
