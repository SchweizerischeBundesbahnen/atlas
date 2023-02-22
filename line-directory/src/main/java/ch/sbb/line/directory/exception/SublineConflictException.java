package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.ValidFromDetail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.entity.SublineVersion.Fields;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SublineConflictException extends AtlasException {

  private static final String CODE_PREFIX = "LIDI.SUBLINE.CONFLICT.";
  private static final String ERROR = "Subline conflict";

  private final SublineVersion newVersion;
  private final List<SublineVersion> overlappingVersions;

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

  private Function<SublineVersion, Detail> toErrorDetail() {
    return lineVersion -> ValidFromDetail.builder()
        .field(Fields.swissSublineNumber)
        .message("SwissSublineNumber {0} already taken from {1} to {2} by {3}")
        .displayInfo(builder()
            .code(CODE_PREFIX + "SWISS_NUMBER")
            .with(Fields.swissSublineNumber, newVersion.getSwissSublineNumber())
            .with(Fields.validFrom, lineVersion.getValidFrom())
            .with(Fields.validTo, lineVersion.getValidTo())
            .with(Fields.slnid, lineVersion.getSlnid())
            .build()).build();
  }

}
