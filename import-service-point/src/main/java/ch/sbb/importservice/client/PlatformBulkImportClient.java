package ch.sbb.importservice.client;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.prm.PlatformBulkImportApiV1;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "platformBulkImportClient", url = "${atlas.client.gateway.url}", path = "service-point-directory",
configuration = TokenPassingFeignClientConfig.class)
public interface PlatformBulkImportClient extends PlatformBulkImportApiV1 {

}
