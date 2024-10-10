package ch.sbb.prm.directory.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.prm.directory.controller.model.PrmObjectRequestParams;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
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

  private Pageable pageable;
  private PrmObjectRequestParams prmObjectRequestParams;

  public Specification<T> getSpecification() {
    return specBuilder().searchCriteriaSpecification(Collections.emptyList())
        .and(specBuilder().validOnSpecification(Optional.ofNullable(prmObjectRequestParams.getValidOn())))
        .and(specBuilder().inSpecification(prmObjectRequestParams.getNumbers(), BasePrmEntityVersion.Fields.number))
        .and(specBuilder().inSpecification(prmObjectRequestParams.getSloids(), BasePrmEntityVersion.Fields.sloid))
        .and(specBuilder().inSpecification(prmObjectRequestParams.getParentServicePointSloids(),
            BasePrmEntityVersion.Fields.parentServicePointSloid))
        .and(specBuilder().inSpecification(prmObjectRequestParams.getStatusRestrictions(), BasePrmEntityVersion.Fields.status))
        .and(new ValidOrEditionTimerangeSpecification<>(
            prmObjectRequestParams.getFromDate(),
            prmObjectRequestParams.getToDate(),
            prmObjectRequestParams.getValidToFromDate(),
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
