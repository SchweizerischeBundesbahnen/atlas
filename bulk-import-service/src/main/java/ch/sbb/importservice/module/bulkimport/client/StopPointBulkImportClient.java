package ch.sbb.importservice.module.bulkimport.client;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.prm.StopPointBulkImportApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "stopPointBulkImportClient", url = "${atlas.client.gateway.url}", path = "prm-directory",
    configuration = TokenPassingFeignClientConfig.class)
public interface StopPointBulkImportClient extends StopPointBulkImportApi {

}
