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
public interface SePoDiAdminClient {

  String BASEPATH = "/service-point-directory/v1/service-points/";

  @PutMapping(value = BASEPATH + "status/{sloid}/{id}")
  ReadServicePointVersionModel postServicePointsStatusUpdate(@PathVariable String sloid, @PathVariable Long id,
      @RequestBody Status status);

  @PutMapping(value = BASEPATH + "/update-designation-official/{id}")
  ReadServicePointVersionModel updateServicePointDesignationOfficial(
          @PathVariable("id") Long id,
          @RequestBody @Valid UpdateDesignationOfficialServicePointModel updateDesignationOfficialServicePointModel
  );
}
