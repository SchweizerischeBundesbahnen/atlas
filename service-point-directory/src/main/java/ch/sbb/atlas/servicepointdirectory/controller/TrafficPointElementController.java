package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.base.service.model.api.Container;
import ch.sbb.atlas.base.service.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.api.TrafficPointElementApiV1;
import ch.sbb.atlas.servicepointdirectory.api.TrafficPointElementVersionModel;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TrafficPointElementController implements TrafficPointElementApiV1 {

  private final TrafficPointElementService trafficPointElementService;

  @Override
  // TODO: add filter parameter
  public Container<TrafficPointElementVersionModel> getTrafficPointElements(Pageable pageable, Optional<LocalDate> validOn) {
    Page<TrafficPointElementVersion> TrafficPointElementVersions = trafficPointElementService.findAll(pageable);
    return Container.<TrafficPointElementVersionModel>builder()
        .objects(TrafficPointElementVersions.stream().map(TrafficPointElementVersionModel::fromEntity).toList())
        .totalCount(TrafficPointElementVersions.getTotalElements())
        .build();
  }

  @Override
  public List<TrafficPointElementVersionModel> getTrafficPointElement(String sloid) {
    List<TrafficPointElementVersionModel> TrafficPointElementVersions =
        trafficPointElementService.findTrafficPointElementVersions(
            sloid)
        .stream()
        .map(TrafficPointElementVersionModel::fromEntity).toList();
    if (TrafficPointElementVersions.isEmpty()) {
      throw new SloidNotFoundException(sloid);
    }
    return TrafficPointElementVersions;
  }

  @Override
  public TrafficPointElementVersionModel getTrafficPointElementVersion(Long id) {
    return trafficPointElementService.findById(id).map(TrafficPointElementVersionModel::fromEntity)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

}
