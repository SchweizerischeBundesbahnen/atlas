package ch.sbb.importservice.controller.geo;

import ch.sbb.importservice.service.geo.ServicePointUpdateGeoLocationJobService;
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
  public void startServicePointImportBatch() throws JobExecutionException {
    updateServicePointGeoJob.runGeoLocationUpdateJob();
  }
}
