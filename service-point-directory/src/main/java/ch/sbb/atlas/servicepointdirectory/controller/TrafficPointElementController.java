package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointImportRequestModel;
import ch.sbb.atlas.imports.servicepoint.trafficpoint.TrafficPointItemImportResult;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.api.TrafficPointElementApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.exception.SloidsNotEqualException;
import ch.sbb.atlas.servicepointdirectory.mapper.TrafficPointElementVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementImportService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class TrafficPointElementController implements TrafficPointElementApiV1 {

  private final TrafficPointElementService trafficPointElementService;
  private final ServicePointService servicePointService;
  private final TrafficPointElementValidationService trafficPointElementValidationService;
  private final TrafficPointElementImportService trafficPointElementImportService;

  @Override
  public Container<ReadTrafficPointElementVersionModel> getTrafficPointElements(Pageable pageable, Map<String, String> searchCriteria,
                                                                            Optional<LocalDate> validOn) {

    Map<String, List<String>> searchCriterias = new HashMap<>();
      //TODO: Clean Code
      for(Map.Entry<String, String> entry : searchCriteria.entrySet()){
        if (!entry.getKey().equals("page") && !entry.getKey().equals("size")){
          String key = entry.getKey();
          List<String> values = new ArrayList<>();
          String[] splittedValues = entry.getValue().split(",");

          for (String value : splittedValues){
            values.add(value);
          }
          searchCriterias.put(key, values);
        }
      }

    Page<TrafficPointElementVersion> trafficPointElementVersions = trafficPointElementService.findAll(
        TrafficPointElementSearchRestrictions.builder()
            .pageable(pageable)
            .searchCriterias(searchCriterias)
            .validOn(validOn)
            .build());
    return Container.<ReadTrafficPointElementVersionModel>builder()
        .objects(trafficPointElementVersions.stream().map(TrafficPointElementVersionMapper::toModel).toList())
        .totalCount(trafficPointElementVersions.getTotalElements())
        .build();
  }

  @Override
  public List<ReadTrafficPointElementVersionModel> getTrafficPointElement(String sloid) {
    List<ReadTrafficPointElementVersionModel> trafficPointElementVersions =
        trafficPointElementService.findBySloidOrderByValidFrom(
                sloid)
            .stream()
            .map(TrafficPointElementVersionMapper::toModel).toList();
    if (trafficPointElementVersions.isEmpty()) {
      throw new SloidNotFoundException(sloid);
    }
    return trafficPointElementVersions;
  }

  @Override
  public ReadTrafficPointElementVersionModel getTrafficPointElementVersion(Long id) {
    return trafficPointElementService.findById(id).map(TrafficPointElementVersionMapper::toModel)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  @Override
  public List<TrafficPointItemImportResult> importTrafficPoints(TrafficPointImportRequestModel trafficPointImportRequestModel) {
    return trafficPointElementImportService.importTrafficPoints(
        trafficPointImportRequestModel.getTrafficPointCsvModelContainers());
  }

  @Override
  public ReadTrafficPointElementVersionModel createTrafficPoint(CreateTrafficPointElementVersionModel trafficPointElementVersionModel) {
    return TrafficPointElementVersionMapper.toModel(
            createTrafficPoint(TrafficPointElementVersionMapper.toEntity(trafficPointElementVersionModel)));
  }

  @Override
  public List<ReadTrafficPointElementVersionModel> updateTrafficPoint(Long id, CreateTrafficPointElementVersionModel trafficPointElementVersionModel) {
    TrafficPointElementVersion trafficPointElementVersionToUpdate = trafficPointElementService.findById(id)
            .orElseThrow(() -> new IdNotFoundException(id));

    if (!trafficPointElementVersionToUpdate.getSloid().equals(trafficPointElementVersionModel.getSloid())) {
      String exceptionMessage = "Sloid for provided id: " + trafficPointElementVersionToUpdate.getSloid() +
              " and sloid in the request body: " + trafficPointElementVersionModel.getSloid() + " are not equal.";
        throw new SloidsNotEqualException(exceptionMessage);
    }

    update(trafficPointElementVersionToUpdate,
            TrafficPointElementVersionMapper.toEntity(trafficPointElementVersionModel));

    return trafficPointElementService.findBySloidOrderByValidFrom(trafficPointElementVersionToUpdate.getSloid())
            .stream()
            .map(TrafficPointElementVersionMapper::toModel)
            .toList();
  }

  private TrafficPointElementVersion createTrafficPoint(TrafficPointElementVersion trafficPointElementVersion) {
    ServicePointNumber servicePointNumber = trafficPointElementVersion.getServicePointNumber();
    trafficPointElementValidationService.validateServicePointNumberExists(trafficPointElementVersion.getServicePointNumber());
    return trafficPointElementService.checkPermissionRightsAndSave(trafficPointElementVersion, servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber));
  }

  private void update(TrafficPointElementVersion currentVersion, TrafficPointElementVersion editedVersion) {
    ServicePointNumber servicePointNumber = editedVersion.getServicePointNumber();
    trafficPointElementValidationService.validateServicePointNumberExists(editedVersion.getServicePointNumber());
    List<ServicePointVersion> allServicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber);
    trafficPointElementService.checkPermissionRightsAndUpdate(currentVersion, editedVersion, allServicePointVersions);
  }

}
