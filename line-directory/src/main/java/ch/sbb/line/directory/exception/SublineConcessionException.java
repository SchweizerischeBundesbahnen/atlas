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
public class SublineConcessionException extends AtlasException {

  private static final String CODE = "LIDI.SUBLINE.ERROR.CONCESSION";
  private static final String MESSAGE = "ConcessionType only allowed on SublineType CONCESSION";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(MESSAGE)
        .error(MESSAGE)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private Set<Detail> getErrorDetails() {
    return Set.of(Detail.builder()
        .field(Fields.concessionType)
        .message(MESSAGE)
        .displayInfo(builder()
            .code(CODE)
            .build()).build());
  }

}
