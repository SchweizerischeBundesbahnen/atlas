package ch.sbb.importservice.exception;

import ch.sbb.atlas.base.service.model.api.ErrorResponse;
import ch.sbb.atlas.base.service.model.api.ErrorResponse.Detail;
import ch.sbb.atlas.base.service.model.api.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.base.service.model.exception.AtlasException;
import java.util.List;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class JobExecutionException extends AtlasException {

  private static final String CODE_PREFIX = "JOB.IMPORT.EXECUTION.FAILED";
  private static final String ERROR = "Job execution failed";

  private final String jobName;
  private final String errorMessage;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message("Import job execution failed")
        .error(ERROR)
        .details(new TreeSet<>(getErrorDetails()))
        .build();
  }

  private List<Detail> getErrorDetails() {
    return List.of(Detail.builder()
        .message(errorMessage)
        .displayInfo(DisplayInfo.builder()
            .code(CODE_PREFIX)
            .with("Job name", jobName)
            .build())
        .build());
  }
}
