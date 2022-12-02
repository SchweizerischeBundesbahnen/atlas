package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.servicepointdirectory.api.ServicePointApiV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointController implements ServicePointApiV1 {

  @Override
  public Integer getServicePointCount() {
    return 350000; /* might be */
  }
}
