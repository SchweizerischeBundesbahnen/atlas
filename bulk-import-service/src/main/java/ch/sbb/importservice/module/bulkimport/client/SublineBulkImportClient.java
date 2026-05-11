package ch.sbb.importservice.module.bulkimport.client;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.lidi.SublineBulkImportApiInternal;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "sublineBulkImportClient", url = "${atlas.client.gateway.url}", path = "line-directory",
    configuration = TokenPassingFeignClientConfig.class)
public interface SublineBulkImportClient extends SublineBulkImportApiInternal {

}

