package ch.sbb.importservice.service;

import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.base.service.imports.servicepoint.model.ServicePointItemImportResult;
import ch.sbb.importservice.client.SePoDiClient;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SePoDiClientService extends BaseClientService {

  private final SePoDiClient sePoDiClient;

  public SePoDiClientService(SePoDiClient sePoDiClient) {
    this.sePoDiClient = sePoDiClient;
    this.clientName = "sePoDiClient";
  }

  //  public List<ServicePointImportResult> postServicePoints() {
  //    return executeRequest(sePoDiClient.postServicePoints(), "Update Service Point");
  //  }

  public List<ServicePointItemImportResult> getServicePoints(ServicePointImportReqModel servicePointImportReqModel) {
    return executeRequest(sePoDiClient.postServicePointsImport(servicePointImportReqModel), "Get Service Point");
  }

}
