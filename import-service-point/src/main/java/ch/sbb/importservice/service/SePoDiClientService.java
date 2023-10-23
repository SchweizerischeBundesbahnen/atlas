package ch.sbb.importservice.service;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
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

  public List<ItemImportResult> postServicePoints(ServicePointImportRequestModel servicePointImportRequestModel) {
    return sePoDiClient.postServicePointsImport(servicePointImportRequestModel);
  }

  public List<ItemImportResult> postTrafficPoints(TrafficPointImportRequestModel trafficPointImportRequestModel) {
    return sePoDiClient.postTrafficPointsImport(trafficPointImportRequestModel);
  }

  public List<ItemImportResult> postLoadingPoints(LoadingPointImportRequestModel loadingPointImportRequestModel) {
    return sePoDiClient.postLoadingPointsImport(loadingPointImportRequestModel);
  }

}
