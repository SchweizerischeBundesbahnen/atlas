package ch.sbb.atlas.api.client.line.workflow;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.lidi.workflow.LineWorkflowApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "lineWorkflowClient", url = "${atlas.client.gateway.url}", path = "line-directory", configuration =
    TokenPassingFeignClientConfig.class)
public interface LineWorkflowClient extends LineWorkflowApi {

}
