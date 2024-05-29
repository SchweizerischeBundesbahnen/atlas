package ch.sbb.workflow.client;

import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.workflow.config.OAuthFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sepodiClient", url = "${atlas.client.gateway.url}", configuration = OAuthFeignConfig.class)
public interface SePoDiClient {

  String BASEPATH = "/service-point-directory/v1/service-points/";

  @PutMapping(value = BASEPATH + "status/{id}")
  ResponseEntity<UpdateServicePointVersionModel> postServicePointsStatusUpdate(@PathVariable("id") Long id,
      @RequestBody Status status);

}
