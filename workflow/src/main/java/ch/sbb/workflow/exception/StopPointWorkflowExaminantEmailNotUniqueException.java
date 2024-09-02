package ch.sbb.workflow.exception;

import static ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo.builder;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import org.springframework.http.HttpStatus;

public class StopPointWorkflowExaminantEmailNotUniqueException extends AtlasException {

  public static final String UNIQUE_EMAIL_MESSAGE = "Email of the workflow Examinants must be unique.";

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(UNIQUE_EMAIL_MESSAGE)
        .error("StopPoint Workflow error")
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message("Email is not unique, it is already used by another examinant.")
        .field("examinantMail")
        .displayInfo(builder()
            .code("WORKFLOW.ERROR.EMAIL_MUST_BE_UNIQUE")
            .build())
        .build());
  }

}
