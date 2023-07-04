package ch.sbb.importservice.client;

import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.servicepoint.ServicePointItemImportResult;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointItemImportResult;
import ch.sbb.importservice.config.OAuthFeignConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sepodiClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface SePoDiClient {

  @PostMapping(value = "/service-point-directory/v1/service-points/import")
  List<ServicePointItemImportResult> postServicePointsImport(
      @RequestBody ServicePointImportRequestModel servicePointImportRequestModel);

  @PostMapping(value = "/service-point-directory/v1/traffic-point-elements/import")
  List<TrafficPointItemImportResult> postTrafficPointsImport(
      @RequestBody TrafficPointImportRequestModel trafficPointImportRequestModel);

}
