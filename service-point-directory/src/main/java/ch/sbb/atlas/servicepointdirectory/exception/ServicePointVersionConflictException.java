package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.api.model.ErrorResponse.ValidFromDetail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.Fields;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ServicePointVersionConflictException extends AtlasException {

  private static final String CODE_PREFIX = "SERVICEPOINTNUMBER.CONFLICT.";
  private static final String ERROR = "ServicePointVersion conflict";

  private final ServicePointVersion newVersion;
  private final List<ServicePointVersion> overlappingVersions;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .error(ERROR)
        .message("A conflict occurred due to a business rule")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    List<Detail> details = new ArrayList<>();

    overlappingVersions.stream()
        .filter(version -> Objects.equals(version.getNumber(), newVersion.getNumber()))
        .forEach(version -> details.add(toNumberOverlapDetail(version)));

    return details;
  }

  private Detail toNumberOverlapDetail(ServicePointVersion version) {
    return ValidFromDetail.builder()
        .field(Fields.number)
        .message("Number {0} already taken from {1} to {2}")
        .displayInfo(DisplayInfo.builder()
            .code(CODE_PREFIX + "NUMBER")
            .with(Fields.number, newVersion.getNumber().getNumber().toString())
            .with(Fields.validFrom, version.getValidFrom())
            .with(Fields.validTo, version.getValidTo())
            .build()).build();
  }
}
