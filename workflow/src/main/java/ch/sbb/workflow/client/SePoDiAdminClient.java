package ch.sbb.workflow.client;

import ch.sbb.workflow.config.AtlasAdminFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * Uses backend client credentials with admin role
 */
@FeignClient(name = "sepodiAdminClient", url = "${atlas.client.gateway.url}", configuration = AtlasAdminFeignConfig.class)
public interface SePoDiAdminClient extends SePoDiApi {}
