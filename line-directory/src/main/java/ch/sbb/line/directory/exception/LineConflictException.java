package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.ValidFromDetail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.entity.LineVersion.Fields;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class LineConflictException extends AtlasException {

  private static final String CODE_PREFIX = "LIDI.LINE.CONFLICT.";
  private static final String ERROR = "Line conflict";

  private final LineVersion newVersion;
  private final List<LineVersion> overlappingVersions;

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

  private Function<LineVersion, Detail> toErrorDetail() {
    return lineVersion -> ValidFromDetail.builder()
        .field(Fields.swissLineNumber)
        .message("SwissLineNumber {0} already taken from {1} to {2} by {3}")
        .displayInfo(builder()
            .code(CODE_PREFIX + "SWISS_NUMBER")
            .with(Fields.swissLineNumber, newVersion.getSwissLineNumber())
            .with(Fields.validFrom, lineVersion.getValidFrom())
            .with(Fields.validTo, lineVersion.getValidTo())
            .with(Fields.slnid, lineVersion.getSlnid())
            .build()).build();
  }

}
