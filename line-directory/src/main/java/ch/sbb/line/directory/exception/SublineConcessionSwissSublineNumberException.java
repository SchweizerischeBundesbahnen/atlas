package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.entity.SublineVersion.Fields;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class SublineConcessionSwissSublineNumberException extends AtlasException {

  private static final String CODE = "LIDI.SUBLINE.ERROR.SWISS_SUBLINE_NUMBER";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("SwissSublineNumber only allowed on SublineType CONCESSION, but then mandatory")
        .error("SwissSublineNumber only allowed on SublineType CONCESSION, but then mandatory")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return Set.of(Detail.builder()
        .field(Fields.swissSublineNumber)
        .message("SwissSublineNumber only allowed on SublineType CONCESSION, but then mandatory")
        .displayInfo(builder()
            .code(CODE)
            .build()).build());
  }

}
