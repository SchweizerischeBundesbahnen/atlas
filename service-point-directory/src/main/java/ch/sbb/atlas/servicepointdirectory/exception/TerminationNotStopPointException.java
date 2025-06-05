package ch.sbb.atlas.servicepointdirectory.exception;

import org.springframework.http.HttpStatus;

public class TerminationNotStopPointException extends BaseException {

  @Override
  protected int getHttpStatus() {
    return HttpStatus.PRECONDITION_FAILED.value();
  }

  @Override
  protected String getCustomMessage() {
    return "A termination workflow is not allowed when the service point version is not a stop point";
  }

  @Override
  protected String getCustomError() {
    return "Termination workflow not allowed";
  }

}
