package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointItemImportResult;
import ch.sbb.atlas.api.servicepoint.TrafficPointElementVersionModel;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.api.TrafficPointElementApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.TrafficPointElementVerisionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementImportService;
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
  private final TrafficPointElementImportService trafficPointElementImportService;

  @Override
  public Container<TrafficPointElementVersionModel> getTrafficPointElements(Pageable pageable, List<String> searchCriteria,
      Optional<LocalDate> validOn) {
    Page<TrafficPointElementVersion> trafficPointElementVersions = trafficPointElementService.findAll(
        TrafficPointElementSearchRestrictions.builder()
            .pageable(pageable)
            .searchCriterias(searchCriteria)
            .validOn(validOn)
            .build());
    return Container.<TrafficPointElementVersionModel>builder()
        .objects(trafficPointElementVersions.stream().map(TrafficPointElementVerisionMapper::fromEntity).toList())
        .totalCount(trafficPointElementVersions.getTotalElements())
        .build();
  }

  @Override
  public List<TrafficPointElementVersionModel> getTrafficPointElement(String sloid) {
    List<TrafficPointElementVersionModel> trafficPointElementVersions =
        trafficPointElementService.findBySloidOrderByValidFrom(
                sloid)
            .stream()
            .map(TrafficPointElementVerisionMapper::fromEntity).toList();
    if (trafficPointElementVersions.isEmpty()) {
      throw new SloidNotFoundException(sloid);
    }
    return trafficPointElementVersions;
  }

  @Override
  public TrafficPointElementVersionModel getTrafficPointElementVersion(Long id) {
    return trafficPointElementService.findById(id).map(TrafficPointElementVerisionMapper::fromEntity)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  @Override
  public List<TrafficPointItemImportResult> importTrafficPoints(TrafficPointImportRequestModel trafficPointImportRequestModel) {
    return trafficPointElementImportService.importTrafficPoints(
        trafficPointImportRequestModel.getTrafficPointCsvModelContainers());
  }

}
