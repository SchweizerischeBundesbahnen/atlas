package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.servicepointdirectory.geodata.protobuf.VectorTile;
import ch.sbb.atlas.servicepointdirectory.geodata.service.ServicePointGeoDataService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin("*")
public class ServicePointsGeoDataController {

  private final ServicePointGeoDataService servicePointGeoDataService;

  @GetMapping(value = "/v1/geodata/servicepoints/{z}/{x}/{y}.pbf", produces = {
      "application/x-protobuf"})
  public VectorTile.Tile getServicePointsGeoData(@PathVariable("z") Integer z,
      @PathVariable("x") Integer x, @PathVariable("y") Integer y) {
    return servicePointGeoDataService.getGeoData(z, x, y, LocalDate.now());
  }
}
