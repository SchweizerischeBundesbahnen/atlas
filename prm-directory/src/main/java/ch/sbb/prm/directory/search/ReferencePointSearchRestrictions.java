package ch.sbb.prm.directory.search;

import ch.sbb.prm.directory.controller.model.ReferencePointRequestParams;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion_;
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
public class ReferencePointSearchRestrictions extends BasePrmSearchRestrictions<ReferencePointVersion> {

  private final Pageable pageable;
  private final ReferencePointRequestParams referencePointRequestParams;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;

  @Override
  public Specification<ReferencePointVersion> getSpecification() {
    super.setPageable(pageable);
    super.setPrmObjectRequestParams(referencePointRequestParams);
    return Specification.allOf(super.getSpecification())
        .and(specBuilder().enumSpecification(referencePointRequestParams.getReferencePointAttributeTypes(),
            ReferencePointVersion_.referencePointType));
  }

}
