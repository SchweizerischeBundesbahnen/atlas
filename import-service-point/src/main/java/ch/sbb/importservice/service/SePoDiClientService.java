package ch.sbb.importservice.service;

import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.importservice.client.SePoDiClient;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SePoDiClientService {

  private final SePoDiClient sePoDiClient;

  public List<ServicePointItemImportResult> getServicePoints(ServicePointImportReqModel servicePointImportReqModel) {
    int size = servicePointImportReqModel.getServicePointCsvModelContainers().size();
    log.info("Executing service point post with {} ServicePoints...", size);
    List<ServicePointItemImportResult> servicePointItemImportResults = sePoDiClient.postServicePointsImport(
        servicePointImportReqModel);
    log.info("Executed {} ServicePoints calls.", servicePointItemImportResults.size());
    return servicePointItemImportResults;
  }

}
