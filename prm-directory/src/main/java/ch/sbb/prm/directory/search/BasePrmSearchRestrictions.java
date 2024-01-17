package ch.sbb.prm.directory.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion.Fields;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion_;
import java.util.Collections;
import java.util.Optional;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@SuperBuilder
@Data
public abstract class BasePrmSearchRestrictions<T extends BasePrmEntityVersion> {

  private final Pageable pageable;
  private final PrmObjectRequestParams prmObjectRequestParams;

  public Specification<T> getSpecification() {
    return specBuilder().searchCriteriaSpecification(Collections.emptyList())
        .and(specBuilder().validOnSpecification(Optional.ofNullable(prmObjectRequestParams.getValidOn())))
        .and(specBuilder().inSpecification(prmObjectRequestParams.getNumbers(), Fields.number))
        .and(specBuilder().inSpecification(prmObjectRequestParams.getSloids(), Fields.sloid))
        .and(specBuilder().inSpecification(prmObjectRequestParams.getParentServicePointSloids(), Fields.parentServicePointSloid))
        .and(new ValidOrEditionTimerangeSpecification<>(
            prmObjectRequestParams.getFromDate(),
            prmObjectRequestParams.getToDate(),
            prmObjectRequestParams.getCreatedAfter(),
            prmObjectRequestParams.getModifiedAfter()));

  }

  protected SpecificationBuilder<T> specBuilder() {
    return SpecificationBuilder.<T>builder()
        .validFromAttribute(BasePrmEntityVersion_.validFrom)
        .validToAttribute(BasePrmEntityVersion_.validTo)
        .build();
  }
}
