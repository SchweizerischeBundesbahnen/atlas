package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile;
import ch.sbb.atlas.servicepointdirectory.geodata.service.ServicePointGeoDataService;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ServicePointsGeoDataController {

  private final ServicePointGeoDataService servicePointGeoDataService;

  @GetMapping(value = "/v1/geodata/service-points/{z}/{x}/{y}.pbf", produces = {
      "application/x-protobuf"})
  public VectorTile.Tile getServicePointsGeoData(@PathVariable("z") Integer z,
      @PathVariable("x") Integer x, @PathVariable("y") Integer y,
      @RequestParam("validAtDate") Optional<LocalDate> validAtDate) {
    return servicePointGeoDataService.getGeoData(z, x, y, validAtDate.orElse(LocalDate.now())
    );
  }
}
