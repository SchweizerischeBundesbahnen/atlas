package ch.sbb.line.directory.module.line.search;

import ch.sbb.atlas.api.lidi.LineVersionV3RequestParams;
import ch.sbb.atlas.model.entity.BaseVersion_;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.SingleStringSpecification;
import ch.sbb.atlas.searching.specification.ValidOnSpecification;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.entity.LineVersionV3_;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class LineVersionV3SearchRestrictions {

  private final Pageable pageable;
  private final LineVersionV3RequestParams lineVersionRequestParams;

  public Specification<LineVersionV3> getSpecification() {
    return new ValidOnSpecification<LineVersionV3>(lineVersionRequestParams.getValidOn(), LineVersionV3_.validFrom,
        LineVersionV3_.validTo)
        .and(new EnumSpecification<>(lineVersionRequestParams.getStatusRestrictions(), BaseVersion_.status))
        .and(new LineObjectTypeSpecification(lineVersionRequestParams.getTypeRestrictions()))
        .and(new SingleStringSpecification<>(lineVersionRequestParams.getSwissLineNumber(),
            LineVersionV3.Fields.swissLineNumber))
        .and(new SingleStringSpecification<>(lineVersionRequestParams.getBusinessOrganisation(),
            LineVersionV3.Fields.businessOrganisation))
        .and(new ValidOrEditionTimerangeSpecification<>(lineVersionRequestParams));
  }

}
