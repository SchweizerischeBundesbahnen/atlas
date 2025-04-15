package ch.sbb.prm.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class AttentionFieldMeanOfTransportConflictException extends AtlasException {

  private static final String CODE = "PRM.PLATFORM.ATTENTION_FIELD_CONFLICT";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message("A conflict occurred due to a business rule")
        .error("AttentionField only allowed for BUS or TRAM")
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    return new TreeSet<>(Set.of(Detail.builder()
        .message("AttentionField only allowed for BUS or TRAM")
        .displayInfo(builder()
            .code(CODE)
            .build())
        .build()));
  }

}
