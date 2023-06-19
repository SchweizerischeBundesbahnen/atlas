package ch.sbb.line.directory.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.line.directory.entity.Line;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class MergeOrSplitInReviewVersionException extends AtlasException {

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.FORBIDDEN.value())
        .message("Business rule validation failed")
        .error(Status.IN_REVIEW.name() + " version may not be updated with versioning")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message(Status.IN_REVIEW.name() + " version may not be updated with versioning")
        .field(Line.Fields.lineType)
        .displayInfo(builder().code("LIDI.LINE.UPDATE_IN_REVIEW_MERGE_OR_SPLIT").build())
        .build());
  }

}
