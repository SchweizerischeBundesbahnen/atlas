package ch.sbb.business.organisation.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.model.ErrorResponse.ValidFromDetail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion.Fields;
import java.util.List;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class BusinessOrganisationConflictException extends AtlasException {

  static final String FIELD = "field";
  private static final String CODE_PREFIX = "BODI.BUSINESS_ORGANISATION.CONFLICT.";
  private static final String ERROR = "BO conflict";

  private final BusinessOrganisationVersion newVersion;
  private final List<BusinessOrganisationVersion> overlappingVersions;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("A conflict occurred due to a business rule")
        .error(ERROR)
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    SortedSet<Detail> details = new TreeSet<>();

    for (BusinessOrganisationVersion version : overlappingVersions) {
      if (Objects.equals(version.getAbbreviationDe(), newVersion.getAbbreviationDe())) {
        details.add(toOverlapDetail(version, Fields.abbreviationDe,
            BusinessOrganisationVersion::getAbbreviationDe));
      }

      if (Objects.equals(version.getAbbreviationFr(), newVersion.getAbbreviationFr())) {
        details.add(toOverlapDetail(version, Fields.abbreviationFr,
            BusinessOrganisationVersion::getAbbreviationFr));
      }

      if (Objects.equals(version.getAbbreviationIt(), newVersion.getAbbreviationIt())) {
        details.add(toOverlapDetail(version, Fields.abbreviationIt,
            BusinessOrganisationVersion::getAbbreviationIt));
      }

      if (Objects.equals(version.getAbbreviationEn(), newVersion.getAbbreviationEn())) {
        details.add(toOverlapDetail(version, Fields.abbreviationEn,
            BusinessOrganisationVersion::getAbbreviationEn));
      }

      if (Objects.equals(version.getOrganisationNumber(), newVersion.getOrganisationNumber())) {
        details.add(toOverlapDetail(version, Fields.organisationNumber,
            v -> String.valueOf(v.getOrganisationNumber())));
      }
    }

    return details;
  }

  private Detail toOverlapDetail(BusinessOrganisationVersion version, String field,
      Function<BusinessOrganisationVersion, String> valueExtractor) {
    return ValidFromDetail.builder()
        .field(field)
        .message("{0} {1} already taken from {2} to {3} by {4}")
        .displayInfo(DisplayInfo.builder()
            .code(CODE_PREFIX + field.toUpperCase())
            .with(FIELD, field)
            .with(field, valueExtractor.apply(newVersion))
            .with(Fields.validFrom, version.getValidFrom())
            .with(Fields.validTo, version.getValidTo())
            .with(Fields.sboid, version.getSboid())
            .build()).build();
  }

}
