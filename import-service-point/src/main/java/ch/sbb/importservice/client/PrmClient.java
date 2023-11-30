package ch.sbb.importservice.client;

import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.atlas.imports.prm.platform.PlatformImportRequestModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointImportRequestModel;
import ch.sbb.importservice.config.OAuthFeignConfig;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "prmClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface PrmClient {

  @PostMapping(value = "/prm-directory/v1/stop-points/import")
  List<ItemImportResult> postStopPointImport(@RequestBody StopPointImportRequestModel stopPointImportRequestModel);

  @PostMapping(value = "/prm-directory/v1/platforms/import")
  List<ItemImportResult> importPlatforms(@RequestBody PlatformImportRequestModel platformImportRequestModel);

}
