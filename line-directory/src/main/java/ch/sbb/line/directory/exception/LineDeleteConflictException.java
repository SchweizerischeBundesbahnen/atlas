package ch.sbb.line.directory.exception;

import static ch.sbb.line.directory.api.ErrorResponse.DisplayInfo.builder;

import ch.sbb.line.directory.api.ErrorResponse;
import ch.sbb.line.directory.api.ErrorResponse.Detail;
import ch.sbb.line.directory.entity.LineVersion.Fields;
import ch.sbb.line.directory.entity.SublineVersion;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class LineDeleteConflictException extends AtlasException {

  private static final String CODE = "LIDI.LINE.CONFLICT.DELETE";
  private static final String ERROR = "Line delete conflict";

  private final String lineVersionSlnid;
  private final List<SublineVersion> sublineVersions;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message("A line related to a subline cannot be deleted.")
                        .error(ERROR)
                        .details(getErrorDetails())
                        .build();
  }

  private List<Detail> getErrorDetails() {
    List<String> sublineVersionSlnids = sublineVersions.stream()
                                                       .map(SublineVersion::getSlnid)
                                                       .distinct()
                                                       .collect(Collectors.toList());
    return sublineVersionSlnids.stream().map(toErrorDetail()).collect(Collectors.toList());
  }

  private Function<String, Detail> toErrorDetail() {
    return sublineVersionSlnid -> Detail.builder()
                                        .field(Fields.slnid)
                                        .message(
                                            "Line with SLNID {0} is related to Subline SLNID {1}")
                                        .displayInfo(builder()
                                            .code(CODE)
                                            .with(Fields.slnid, this.lineVersionSlnid)
                                            .with(SublineVersion.class.getSimpleName() + "."
                                                + SublineVersion.Fields.slnid, sublineVersionSlnid)
                                            .build()).build();
  }

}
