package ch.sbb.scheduling.service;

import ch.sbb.scheduling.exception.SchedulingExecutionException;
import feign.Response;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public abstract class BaseSchedulerService {

  protected String clientName;

  protected Response executeRequest(Supplier<Response> clientCall, String jobName) {
    log.info("{}: Starting Export {}...", clientName, jobName);
    try (Response response = clientCall.get()) {
      if (HttpStatus.OK.value() == response.status()) {
        log.info("{}: Export {} Successfully completed", clientName, jobName);
      } else {
        log.error("{}: Export {} Unsuccessful completed. Response Status: {} \nResponse: \n{}",
            clientName, jobName,
            response.status(), response);
        throw new SchedulingExecutionException(response);
      }
      return response;
    }
  }

}
