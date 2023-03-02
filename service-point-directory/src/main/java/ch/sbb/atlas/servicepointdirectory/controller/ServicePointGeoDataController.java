package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.servicepointdirectory.api.ServicePointGeoDataApiV1;
import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile;
import ch.sbb.atlas.servicepointdirectory.geodata.service.ServicePointGeoDataService;
import io.swagger.v3.oas.annotations.Hidden;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@Hidden
public class ServicePointGeoDataController implements ServicePointGeoDataApiV1 {

  private final ServicePointGeoDataService servicePointGeoDataService;

  public VectorTile.Tile getServicePointsGeoData(Integer z, Integer x, Integer y, Optional<LocalDate> validAtDate) {
    return servicePointGeoDataService.getGeoData(z, x, y, validAtDate.orElse(LocalDate.now()));
  }
}
