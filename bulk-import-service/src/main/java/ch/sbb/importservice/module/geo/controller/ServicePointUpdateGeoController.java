package ch.sbb.importservice.module.geo.controller;

import ch.sbb.importservice.module.geo.api.ServicePointUpdateGeoApiV1;
import ch.sbb.importservice.module.geo.service.ServicePointUpdateGeoLocationJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ServicePointUpdateGeoController implements ServicePointUpdateGeoApiV1 {

  private final ServicePointUpdateGeoLocationJobService updateServicePointGeoJob;

  @Override
  public void startServicePointUpdateGeoLocation() throws JobExecutionException {
    updateServicePointGeoJob.runGeoLocationUpdateJob();
  }
}
