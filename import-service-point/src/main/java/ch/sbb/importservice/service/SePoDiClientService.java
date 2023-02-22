package ch.sbb.importservice.service;

import ch.sbb.atlas.imports.servicepoint.model.ServicePointImportReqModel;
import ch.sbb.atlas.imports.servicepoint.model.ServicePointItemImportResult;
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

  public List<ServicePointItemImportResult> postServicePoints(ServicePointImportReqModel servicePointImportReqModel) {
    return sePoDiClient.postServicePointsImport(servicePointImportReqModel);
  }

}
