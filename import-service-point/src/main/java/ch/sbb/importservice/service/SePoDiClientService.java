package ch.sbb.importservice.service;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointItemImportResult;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointItemImportResult;
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

  public List<ServicePointItemImportResult> postServicePoints(ServicePointImportRequestModel servicePointImportRequestModel) {
    return sePoDiClient.postServicePointsImport(servicePointImportRequestModel);
  }

  public List<TrafficPointItemImportResult> postTrafficPoints(TrafficPointImportRequestModel trafficPointImportRequestModel) {
    return sePoDiClient.postTrafficPointsImport(trafficPointImportRequestModel);
  }
}
