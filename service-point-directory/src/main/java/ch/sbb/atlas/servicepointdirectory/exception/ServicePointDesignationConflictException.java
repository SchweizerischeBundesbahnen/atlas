package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public abstract class ServicePointDesignationConflictException extends AtlasException {

  protected static final String CODE_PREFIX = "SEPODI.SERVICE_POINTS.CONFLICT.";
  private static final String ERROR = "ServicePoint conflict";

  private final ServicePointVersion newVersion;
  private final List<ServicePointVersion> overlappingVersions;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("A conflict occurred due to a business rule while saving " + newVersion.getNumber().getNumber())
        .error(ERROR)
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    return overlappingVersions.stream().map(toErrorDetail()).collect(Collectors.toCollection(
        TreeSet::new));
  }

  protected abstract Function<ServicePointVersion, Detail> toErrorDetail();

  protected ServicePointVersion getNewVersion() {
    return newVersion;
  }
}
