package ch.sbb.atlas.servicepointdirectory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.ValidFromDetail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion.Fields;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class ServicePointDesignationLongConflictException extends AtlasException {

  private static final String CODE_PREFIX = "SEPODI.SERVICE_POINTS.CONFLICT.";
  private static final String ERROR = "ServicePoint conflict";

  private final ServicePointVersion newVersion;
  private final List<ServicePointVersion> overlappingVersions;

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
    return overlappingVersions.stream().map(toErrorDetail()).collect(Collectors.toCollection(
        TreeSet::new));
  }

  private Function<ServicePointVersion, Detail> toErrorDetail() {
    return servicePointVersion -> ValidFromDetail.builder()
        .field(Fields.designationLong)
        .message("DesignationLong {0} already taken from {1} to {2} by {3}")
        .displayInfo(builder()
            .code(CODE_PREFIX + "DESIGNATION_LONG")
            .with(Fields.designationLong, newVersion.getDesignationLong())
            .with(Fields.validFrom, servicePointVersion.getValidFrom())
            .with(Fields.validTo, servicePointVersion.getValidTo())
            .with(Fields.number, String.valueOf(servicePointVersion.getNumber().getNumber()))
            .build()).build();
  }

}
