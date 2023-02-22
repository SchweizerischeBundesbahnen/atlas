package ch.sbb.workflow.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.workflow.entity.Workflow.Fields;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class BusinessObjectCurrentlyInReviewException extends AtlasException {

  private static final String BUSINESS_OBJECT_IS_ALREADY_IN_REVIEW = "BusinessObject is already in review";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.CONFLICT.value())
        .message(BUSINESS_OBJECT_IS_ALREADY_IN_REVIEW)
        .error(BUSINESS_OBJECT_IS_ALREADY_IN_REVIEW)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message(BUSINESS_OBJECT_IS_ALREADY_IN_REVIEW)
        .field(Fields.businessObjectId)
        .displayInfo(builder().code("WORKFLOW.ERROR.ALREADY_IN_REVIEW").build())
        .build());
  }

}
