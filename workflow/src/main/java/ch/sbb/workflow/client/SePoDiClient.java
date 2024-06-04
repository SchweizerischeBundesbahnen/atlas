package ch.sbb.workflow.client;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.model.Status;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sepodiClient", url = "${atlas.client.gateway.url}", configuration = TokenPassingFeignClientConfig.class)
public interface SePoDiClient {

  String BASEPATH = "/service-point-directory/v1/service-points/";

  @PutMapping(value = BASEPATH + "status/{sloid}/{id}")
  UpdateServicePointVersionModel postServicePointsStatusUpdate(@PathVariable("sloid") String sloid, @PathVariable("id") Long id,
      @RequestBody Status status);

}
