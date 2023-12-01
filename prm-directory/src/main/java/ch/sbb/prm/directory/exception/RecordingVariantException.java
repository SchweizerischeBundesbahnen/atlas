package ch.sbb.prm.directory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class RecordingVariantException extends AtlasException {

  private final Map<String, String> errorConstraintMap;
  private final String objectName;
  private final String sloid;
  private final boolean isReduced;

  private static final String ERROR = "Precondition failed";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(buildErrorMsg())
        .details(getErrorDetails())
        .build();
  }

  private String buildErrorMsg() {
    StringBuilder builder = new StringBuilder(objectName + " with sloid [" + sloid + "] cannot be save: ");
    if(isReduced){
      builder.append("Attempting to save a Reduced object with wrong properties population!");
    }else {
      builder.append("Attempting to save a Complete object with wrong properties population!");
    }
    return builder.toString();
  }

  private SortedSet<Detail> getErrorDetails() {
    SortedSet<Detail> errorsDetail = new TreeSet<>();
    errorConstraintMap.forEach((key, value) -> {
      Detail detail = Detail.builder()
          .field(key)
          .message(value)
          .displayInfo(DisplayInfo.builder().code("ERROR.PRM.RECODING_VARIANTS.BAD_REQUEST").build())
          .build();
      errorsDetail.add(detail);
    });
    return errorsDetail;
  }

}
