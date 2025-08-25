package ch.sbb.atlas.servicepointdirectory.module.trafficpoint.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.api.TrafficPointElementApiInternal;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.mapper.TrafficPointElementVersionMapper;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.service.TrafficPointElementService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TrafficPointElementApiInternalController implements TrafficPointElementApiInternal {

  private final TrafficPointElementService trafficPointElementService;

  @Override
  public Container<ReadTrafficPointElementVersionModel> getAreasOfServicePoint(Integer servicePointNumber, Pageable pageable) {
    return trafficPointElementService.getTrafficPointElementsByServicePointNumber(servicePointNumber, pageable,
        TrafficPointElementType.BOARDING_AREA);
  }

  @Override
  public Container<ReadTrafficPointElementVersionModel> getPlatformsOfServicePoint(Integer servicePointNumber,
      Pageable pageable) {
    return trafficPointElementService.getTrafficPointElementsByServicePointNumber(servicePointNumber, pageable,
        TrafficPointElementType.BOARDING_PLATFORM);
  }

  @Override
  public List<ReadTrafficPointElementVersionModel> getTrafficPointsOfServicePointValidToday(Integer servicePointNumber) {
    return trafficPointElementService.getTrafficPointElementsByServicePointNumber(servicePointNumber, LocalDate.now()).stream()
        .map(TrafficPointElementVersionMapper::toModel).toList();
  }

}
