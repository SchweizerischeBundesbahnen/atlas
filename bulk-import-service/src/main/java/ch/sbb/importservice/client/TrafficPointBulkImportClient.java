package ch.sbb.importservice.client;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.servicepoint.TrafficPointBulkImportApiV1;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "trafficPointBulkImportClient", url = "${atlas.client.gateway.url}", path = "service-point-directory",
configuration = TokenPassingFeignClientConfig.class)
public interface TrafficPointBulkImportClient extends TrafficPointBulkImportApiV1 {

}
