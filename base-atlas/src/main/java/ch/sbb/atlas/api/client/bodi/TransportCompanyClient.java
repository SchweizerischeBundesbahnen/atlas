package ch.sbb.atlas.api.client.bodi;

import ch.sbb.atlas.api.bodi.TransportCompanyApiV1;
import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "transportCompanyClient", url = "${atlas.client.gateway.url}", path = "business-organisation-directory",
    configuration = TokenPassingFeignClientConfig.class)
public interface TransportCompanyClient extends TransportCompanyApiV1 {

}
