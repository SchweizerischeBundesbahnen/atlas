package ch.sbb.importservice.client;

import ch.sbb.atlas.api.servicepoint.ServicePointBulkImportApiV1;
import ch.sbb.importservice.config.OAuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "servicePointBulkImportClient", url = "${atlas.client.gateway.url}", path = "service-point-directory",
    configuration = OAuthFeignConfig.class)
public interface ServicePointBulkImportClient extends ServicePointBulkImportApiV1 {

}
