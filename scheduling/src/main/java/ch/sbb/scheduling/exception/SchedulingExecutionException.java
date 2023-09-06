package ch.sbb.scheduling.exception;

import feign.Response;

public class SchedulingExecutionException extends RuntimeException {

  public SchedulingExecutionException(Response response) {
    super(response.reason());
  }

}
