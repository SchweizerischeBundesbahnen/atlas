package ch.sbb.prm.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.ValidFromDetail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.prm.directory.entity.BasePrmEntityVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion;
import ch.sbb.prm.directory.entity.ReferencePointVersion.Fields;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class MainReferencePointConflictException extends AtlasException {

  private static final String CODE = "PRM.REFERENCE_POINTS.CONFLICT";
  private static final String ERROR = "Main ReferencePoint conflict";

  private final ReferencePointVersion newVersion;
  private final List<ReferencePointVersion> overlappingVersions;

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

  private Function<ReferencePointVersion, Detail> toErrorDetail() {
    return referencePointVersion -> ValidFromDetail.builder()
        .field(Fields.mainReferencePoint)
        .message("Main ReferencePoint already taken from {0} to {1} by {2}")
        .displayInfo(builder()
            .code(CODE)
            .with(BasePrmEntityVersion.Fields.validFrom, referencePointVersion.getValidFrom())
            .with(BasePrmEntityVersion.Fields.validTo, referencePointVersion.getValidTo())
            .with(BasePrmEntityVersion.Fields.sloid, referencePointVersion.getSloid())
            .build()).build();
  }

}
