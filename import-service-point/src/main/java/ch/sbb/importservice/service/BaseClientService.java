package ch.sbb.importservice.service;

import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointItemImportResult;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseClientService {

  protected String clientName;

  protected List<ServicePointItemImportResult> executeRequest(List<ServicePointItemImportResult> clientCall, String jobName) {
    log.info("{}: Starting Export {}...", clientName, jobName);
    //    try (Response response = clientCall) {
    //      if (HttpStatus.OK.value() == response.status()) {
    //        log.info("{}: Export {} Successfully completed", clientName, jobName);
    //      } else {
    //        log.error("{}: Export {} Unsuccessful completed. Response Status: {} \nResponse: \n{}",
    //            clientName, jobName,
    //            response.status(), response);
    //      }
    //      return response;
    //    }
    return clientCall;
  }

}
