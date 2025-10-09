package ch.sbb.line.directory.module.line.search;

import ch.sbb.atlas.api.lidi.LineVersionRequestParams;
import ch.sbb.atlas.model.entity.BaseVersion_;
import ch.sbb.atlas.searching.specification.EnumSpecification;
import ch.sbb.atlas.searching.specification.SingleStringSpecification;
import ch.sbb.atlas.searching.specification.ValidOnSpecification;
import ch.sbb.atlas.searching.specification.ValidOrEditionTimerangeSpecification;
import ch.sbb.line.directory.module.line.entity.Line.Fields;
import ch.sbb.line.directory.module.line.entity.LineVersion;
import ch.sbb.line.directory.module.line.entity.LineVersion_;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Getter
@ToString
@SuperBuilder
public class LineVersionSearchRestrictions {

  private final Pageable pageable;
  private final LineVersionRequestParams lineVersionRequestParams;

  public Specification<LineVersion> getSpecification() {
    return new ValidOnSpecification<>(lineVersionRequestParams.getValidOn(), LineVersion_.validFrom, LineVersion_.validTo)
        .and(new EnumSpecification<>(lineVersionRequestParams.getStatusRestrictions(), BaseVersion_.status))
        .and(new EnumSpecification<>(lineVersionRequestParams.getTypeRestrictions(), LineVersion_.lineType))
        .and(new SingleStringSpecification<>(lineVersionRequestParams.getSwissLineNumber(), Fields.swissLineNumber))
        .and(new SingleStringSpecification<>(lineVersionRequestParams.getBusinessOrganisation(), Fields.businessOrganisation))
        .and(new ValidOrEditionTimerangeSpecification<>(lineVersionRequestParams));
  }

}
