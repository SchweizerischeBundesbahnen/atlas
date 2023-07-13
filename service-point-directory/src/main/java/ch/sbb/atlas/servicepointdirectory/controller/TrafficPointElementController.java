package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointItemImportResult;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepointdirectory.api.TrafficPointElementApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.mapper.TrafficPointElementVerisionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementImportService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TrafficPointElementController implements TrafficPointElementApiV1 {

  private final TrafficPointElementService trafficPointElementService;
  private final TrafficPointElementImportService trafficPointElementImportService;

  @Override
  public Container<ReadTrafficPointElementVersionModel> getTrafficPointElements(Pageable pageable, List<String> searchCriteria,
                                                                         Optional<LocalDate> validOn) {
    Page<TrafficPointElementVersion> trafficPointElementVersions = trafficPointElementService.findAll(
        TrafficPointElementSearchRestrictions.builder()
            .pageable(pageable)
            .searchCriterias(searchCriteria)
            .validOn(validOn)
            .build());
    return Container.<ReadTrafficPointElementVersionModel>builder()
        .objects(trafficPointElementVersions.stream().map(TrafficPointElementVerisionMapper::toModel).toList())
        .totalCount(trafficPointElementVersions.getTotalElements())
        .build();
  }

  @Override
  public List<ReadTrafficPointElementVersionModel> getTrafficPointElement(String sloid) {
    List<ReadTrafficPointElementVersionModel> trafficPointElementVersions =
        trafficPointElementService.findBySloidOrderByValidFrom(
                sloid)
            .stream()
            .map(TrafficPointElementVerisionMapper::toModel).toList();
    if (trafficPointElementVersions.isEmpty()) {
      throw new SloidNotFoundException(sloid);
    }
    return trafficPointElementVersions;
  }

  @Override
  public ReadTrafficPointElementVersionModel getTrafficPointElementVersion(Long id) {
    return trafficPointElementService.findById(id).map(TrafficPointElementVerisionMapper::toModel)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  @Override
  public List<TrafficPointItemImportResult> importTrafficPoints(TrafficPointImportRequestModel trafficPointImportRequestModel) {
    return trafficPointElementImportService.importTrafficPoints(
        trafficPointImportRequestModel.getTrafficPointCsvModelContainers());
  }

  @Override
  public ReadTrafficPointElementVersionModel createTrafficPoint(CreateTrafficPointElementVersionModel trafficPointElementVersionModel) {
    return TrafficPointElementVerisionMapper.toModel(
            trafficPointElementService.save(TrafficPointElementVerisionMapper.toEntity(trafficPointElementVersionModel)));
  }

  @Override
  public List<ReadTrafficPointElementVersionModel> updateTrafficPoint(Long id, CreateTrafficPointElementVersionModel trafficPointElementVersionModel) {
    TrafficPointElementVersion trafficPointElementVersionToUpdate = trafficPointElementService.findById(id)
            .orElseThrow(() -> new IdNotFoundException(id));

    trafficPointElementService.updateTrafficPointElementVersion(trafficPointElementVersionToUpdate,
            TrafficPointElementVerisionMapper.toEntity(trafficPointElementVersionModel));

    return trafficPointElementService.findBySloidOrderByValidFrom(trafficPointElementVersionToUpdate.getSloid())
            .stream()
            .map(TrafficPointElementVerisionMapper::toModel)
            .toList();
  }

}
