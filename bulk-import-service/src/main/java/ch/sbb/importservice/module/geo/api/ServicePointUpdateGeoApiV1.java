package ch.sbb.importservice.module.geo.api;

import ch.sbb.atlas.annotation.AdminOnly;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.batch.core.job.JobExecutionException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Service Point Update Geo")
@RequestMapping("v1/service-point-job")
public interface ServicePointUpdateGeoApiV1 {

  @AdminOnly
  @PostMapping("update-geo")
  void startServicePointUpdateGeoLocation() throws JobExecutionException;

}
