package ch.sbb.workflow.service.lidi;

import ch.sbb.line.directory.workflow.api.LineWorkflowApi;
import ch.sbb.workflow.config.TokenPassingFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "lineWorkflowClient", url = "localhost:8888", path = "line-directory", configuration =
    TokenPassingFeignConfig.class)
public interface LineWorkflowClient extends LineWorkflowApi {

}
