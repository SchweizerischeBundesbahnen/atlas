package ch.sbb.line.directory.module.subline.search;

import ch.sbb.atlas.api.lidi.SublineVersionRequestParams;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.SingleStringSpecification;
import ch.sbb.atlas.searching.specification.ValidOnSpecification;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.line.directory.module.subline.entity.SublineVersion;
import ch.sbb.line.directory.module.subline.entity.SublineVersion.Fields;
import ch.sbb.line.directory.module.subline.entity.SublineVersion_;
import java.util.Optional;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class SublineVersionSearchRestrictions {

  private final Pageable pageable;
  private final SublineVersionRequestParams sublineVersionRequestParams;

  public Specification<SublineVersion> getSpecification() {
    return new ValidOnSpecification<>(Optional.ofNullable(sublineVersionRequestParams.getValidOn()), SublineVersion_.validFrom,
        SublineVersion_.validTo)
        .and(new EnumSpecification<>(sublineVersionRequestParams.getTypeRestrictions(), SublineVersion_.sublineType))
        .and(new SingleStringSpecification<>(Optional.ofNullable(sublineVersionRequestParams.getSwissSublineNumber()),
            Fields.swissSublineNumber))
        .and(new SingleStringSpecification<>(Optional.ofNullable(sublineVersionRequestParams.getMainlineSlnid()),
            Fields.mainlineSlnid))
        .and(new SingleStringSpecification<>(Optional.ofNullable(sublineVersionRequestParams.getBusinessOrganisation()),
            Fields.businessOrganisation))
        .and(new ValidOrEditionTimerangeSpecification<>(sublineVersionRequestParams));
  }

}
