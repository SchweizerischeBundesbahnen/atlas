package ch.sbb.importservice.client;

import ch.sbb.atlas.imports.BulkImportContainer;
import ch.sbb.atlas.imports.ItemImportResult;
import ch.sbb.importservice.config.OAuthFeignConfig;
import ch.sbb.importservice.model.ImportType;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sepodiClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface SePoDiClient {

  @PostMapping(value = "/service-point-directory/v1/service-points/bulk-import/{importType}")
  List<ItemImportResult> bulkImportServicePoints(@PathVariable ImportType importType,
      @RequestBody List<BulkImportContainer> bulkImportContainerList);

}
