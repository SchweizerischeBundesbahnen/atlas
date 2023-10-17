package ch.sbb.importservice.client;

import ch.sbb.atlas.imports.prm.stopplace.StopPlaceImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.ItemImportResult;
import ch.sbb.importservice.config.OAuthFeignConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "prmClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface PrmClient {

  @PostMapping(value = "/prm-directory/v1/stop-places/import")
  List<ItemImportResult> postStopPlacesImport(
      @RequestBody StopPlaceImportRequestModel stopPlaceImportRequestModel);

}
