package ch.sbb.prm.directory.module.bulkimport.client;

import ch.sbb.prm.directory.module.stoppoint.api.StopPointApiV1;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StopPointApiClient {

  private final StopPointApiV1 stopPointApiV1;

  public void terminateStopPoint(String sloid, LocalDate validTo) {
    stopPointApiV1.terminateStopPoint(sloid, validTo);
  }

}
