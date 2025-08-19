package ch.sbb.prm.directory.relation.search;

import ch.sbb.prm.directory.relation.controller.model.RelationRequestParams;
import ch.sbb.prm.directory.relation.entity.RelationVersion;
import ch.sbb.prm.directory.relation.entity.RelationVersion_;
import ch.sbb.prm.directory.search.BasePrmSearchRestrictions;
import java.util.List;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class RelationSearchRestrictions extends BasePrmSearchRestrictions<RelationVersion> {

  private final Pageable pageable;
  private final RelationRequestParams relationRequestParams;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;

  @Override
  public Specification<RelationVersion> getSpecification() {
    super.setPageable(pageable);
    super.setPrmObjectRequestParams(relationRequestParams);
    return Specification.allOf(super.getSpecification())
        .and(specBuilder().enumSpecification(relationRequestParams.getReferencePointElementTypes(),
            RelationVersion_.referencePointElementType))
        .and(specBuilder().inSpecification(relationRequestParams.getReferencePointSloids(),
            RelationVersion.Fields.referencePointSloid));
  }

}
