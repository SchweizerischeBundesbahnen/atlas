package ch.sbb.importservice.client;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.servicepoint.loadingpoint.LoadingPointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.importservice.config.OAuthFeignConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sepodiClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface SePoDiClient {

  @PostMapping(value = "/service-point-directory/v1/service-points/import")
  List<ItemImportResult> postServicePointsImport(
      @RequestBody ServicePointImportRequestModel servicePointImportRequestModel);

  @PostMapping(value = "/service-point-directory/v1/traffic-point-elements/import")
  List<ItemImportResult> postTrafficPointsImport(
      @RequestBody TrafficPointImportRequestModel trafficPointImportRequestModel);

  @PostMapping(value = "/service-point-directory/v1/loading-points/import")
  List<ItemImportResult> postLoadingPointsImport(
      @RequestBody LoadingPointImportRequestModel loadingPointImportRequestModel);

}
