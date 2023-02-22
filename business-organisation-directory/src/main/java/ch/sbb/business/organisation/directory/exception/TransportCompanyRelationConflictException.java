package ch.sbb.business.organisation.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.model.ErrorResponse.ValidFromDetail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation.Fields;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class TransportCompanyRelationConflictException extends AtlasException {

  private final TransportCompanyRelation newRelation;
  private final List<TransportCompanyRelation> overlappingRelations;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("A conflict occurred due to a business rule")
        .error("TransportCompany - BO relation conflict")
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    return overlappingRelations.stream().map(toOverlapDetail()).collect(Collectors.toCollection(
        TreeSet::new));
  }

  private Function<TransportCompanyRelation, Detail> toOverlapDetail() {
    return relation -> ValidFromDetail.builder()
        .field(Fields.sboid)
        .message("TransportCompany {0} already relates to {3} from {1} to {2}")
        .displayInfo(DisplayInfo.builder()
            .code("BODI.TRANSPORT_COMPANIES.RELATION_CONFLICT")
            .with(TransportCompany.Fields.number, relation.getTransportCompany().getNumber())
            .with(Fields.validFrom, relation.getValidFrom())
            .with(Fields.validTo, relation.getValidTo())
            .with(Fields.sboid, relation.getSboid())
            .with(TransportCompany.Fields.abbreviation, relation.getTransportCompany().getAbbreviation())
            .with(TransportCompany.Fields.businessRegisterName, relation.getTransportCompany().getBusinessRegisterName())
            .build()).build();
  }

}
