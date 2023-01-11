package ch.sbb.importservice.service;

import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public abstract class BaseClientService {

  protected String clientName;

  protected Response executeRequest(Response clientCall, String jobName) {
    log.info("{}: Starting Export {}...", clientName, jobName);
    try (Response response = clientCall) {
      if (HttpStatus.OK.value() == response.status()) {
        log.info("{}: Export {} Successfully completed", clientName, jobName);
      } else {
        log.error("{}: Export {} Unsuccessful completed. Response Status: {} \nResponse: \n{}",
            clientName, jobName,
            response.status(), response);
      }
      return response;
    }
  }

}
