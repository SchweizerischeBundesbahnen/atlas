package ch.sbb.importservice.client;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.lidi.LineBulkImportApiInternal;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "lineBulkImportClient", url = "${atlas.client.gateway.url}", path = "line-directory",
    configuration = TokenPassingFeignClientConfig.class)
public interface LineBulkImportClient extends LineBulkImportApiInternal {

}
