package ch.sbb.atlas.api.servicepoint;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Service Points Geo Update")
public interface ServicePointGeoUpdateApiV1 {

  @GetMapping("v1/service-points/bulk-import/actual-swiss-service-point-with-geo")
  List<ServicePointSwissWithGeoModel> getActualServicePointWithGeolocation();
}
