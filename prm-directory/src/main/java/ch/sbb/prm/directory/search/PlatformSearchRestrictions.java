package ch.sbb.prm.directory.search;

import ch.sbb.atlas.searching.SpecificationBuilder;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion.Fields;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion_;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.entity.PlatformVersion_;
import ch.sbb.prm.directory.controller.model.PlatformRequestParams;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@Builder
public class PlatformSearchRestrictions {

  private final Pageable pageable;
  private final PlatformRequestParams platformRequestParams;

  @Singular(ignoreNullCollections = true)
  private List<String> searchCriterias;


  public Specification<PlatformVersion> getSpecification() {
    return specBuilder().searchCriteriaSpecification(searchCriterias)
        .and(specBuilder().validOnSpecification(Optional.ofNullable(platformRequestParams.getValidOn())))
        .and(specBuilder().inSpecification(platformRequestParams.getNumbers(), Fields.number))
        .and(specBuilder().inSpecification(platformRequestParams.getSloids(), Fields.sloid))
        .and(specBuilder().inSpecification(platformRequestParams.getParentServicePointSloids(), Fields.parentServicePointSloid))
        .and(new ValidOrEditionTimerangeSpecification<>(
            platformRequestParams.getFromDate(),
            platformRequestParams.getToDate(),
            platformRequestParams.getCreatedAfter(),
            platformRequestParams.getModifiedAfter()));

  }

  protected SpecificationBuilder<PlatformVersion> specBuilder() {
    return SpecificationBuilder.<PlatformVersion>builder()
        .validFromAttribute(BasePrmEntityVersion_.validFrom)
        .validToAttribute(PlatformVersion_.validTo)
        .build();
  }

}
