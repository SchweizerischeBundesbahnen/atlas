package ch.sbb.scheduling.client;

import ch.sbb.scheduling.config.OAuthFeignConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "workflowClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface WorkflowClient {

  @PostMapping(value = "/workflow/v1/stop-point/workflows/end-expired")
  Response endExpiredWorkflows();

}
