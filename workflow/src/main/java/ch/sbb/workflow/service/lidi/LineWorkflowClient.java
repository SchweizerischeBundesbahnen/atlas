package ch.sbb.workflow.service.lidi;

import ch.sbb.atlas.api.line.workflow.LineWorkflowApi;
import ch.sbb.workflow.config.TokenPassingFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "lineWorkflowClient", url = "${atlas.client.gateway.url}", path = "line-directory", configuration =
    TokenPassingFeignConfig.class)
public interface LineWorkflowClient extends LineWorkflowApi {

}
