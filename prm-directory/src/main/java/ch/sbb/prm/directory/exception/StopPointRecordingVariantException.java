package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.prm.directory.entity.StopPointVersion;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class StopPointRecordingVariantException extends AtlasException {

  private final StopPointVersion stopPointVersion;
  private final Map<String,String> errorConstraintMap;

  private static final String ERROR = "StopPoint precondition failed";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("A reduced StopPoint cannot be save!")
        .error(ERROR)
        .details(getErrorDetails())
        .build();
  }

  private SortedSet<Detail> getErrorDetails() {
    SortedSet<Detail> errorsDetail = new TreeSet<>();
    errorConstraintMap.forEach((key, value) -> {
      Detail detail = Detail.builder()
          .field(key)
          .message(value)
          .displayInfo(DisplayInfo.builder().code("ERROR.PRM.PRECONDITION_FAILED").build())
          .build();
      errorsDetail.add(detail);
    });
    return errorsDetail;
  }

}
