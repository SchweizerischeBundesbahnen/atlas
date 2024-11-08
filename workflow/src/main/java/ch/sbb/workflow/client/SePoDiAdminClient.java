package ch.sbb.workflow.client;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateDesignationOfficialServicePointModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.workflow.config.AtlasAdminFeignConfig;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Uses backend client credentials with admin role
 */
@FeignClient(name = "sepodiAdminClient", url = "${atlas.client.gateway.url}", configuration = AtlasAdminFeignConfig.class)
public interface SePoDiAdminClient extends SePoDiApi {}
