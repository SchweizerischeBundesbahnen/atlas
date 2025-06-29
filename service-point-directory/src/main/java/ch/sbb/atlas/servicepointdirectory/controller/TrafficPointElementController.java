package ch.sbb.atlas.servicepointdirectory.controller;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadTrafficPointElementVersionModel;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.api.TrafficPointElementApiV1;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.exception.SloidsNotEqualException;
import ch.sbb.atlas.servicepointdirectory.helper.TerminationHelper;
import ch.sbb.atlas.servicepointdirectory.mapper.TrafficPointElementVersionMapper;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.CrossValidationService;
import ch.sbb.atlas.servicepointdirectory.service.ServicePointDistributor;
import ch.sbb.atlas.servicepointdirectory.service.georeference.GeoReferenceService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointTerminationService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementRequestParams;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import java.time.LocalDate;
import java.util.List;
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
  private final ServicePointService servicePointService;
  private final ServicePointTerminationService servicePointTerminationService;
  private final CrossValidationService crossValidationService;
  private final ServicePointDistributor servicePointDistributor;
  private final GeoReferenceService geoReferenceService;

  @Override
  public Container<ReadTrafficPointElementVersionModel> getTrafficPointElements(Pageable pageable,
      TrafficPointElementRequestParams trafficPointElementRequestParams) {

    TrafficPointElementSearchRestrictions trafficPointElementSearchRestrictions = TrafficPointElementSearchRestrictions.builder()
        .pageable(pageable)
        .trafficPointElementRequestParams(trafficPointElementRequestParams)
        .build();

    Page<TrafficPointElementVersion> trafficPointElementVersions = trafficPointElementService.findAll(
        trafficPointElementSearchRestrictions);

    return Container.<ReadTrafficPointElementVersionModel>builder()
        .objects(trafficPointElementVersions.stream()
            .map(TrafficPointElementVersionMapper::toModel)
            .toList())
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

  @Override
  public ReadTrafficPointElementVersionModel getTrafficPointElementVersion(Long id) {
    return trafficPointElementService.findById(id).map(TrafficPointElementVersionMapper::toModel)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  @Override
  public ReadTrafficPointElementVersionModel createTrafficPoint(
      CreateTrafficPointElementVersionModel trafficPointElementVersionModel) {
    TrafficPointElementVersion createdTrafficPoint = createTrafficPoint(
        TrafficPointElementVersionMapper.toEntity(trafficPointElementVersionModel));
    servicePointDistributor.publishTrafficPointElement(createdTrafficPoint);
    return TrafficPointElementVersionMapper.toModel(createdTrafficPoint);
  }

  @Override
  public List<ReadTrafficPointElementVersionModel> updateTrafficPoint(Long id,
      CreateTrafficPointElementVersionModel trafficPointElementVersionModel) {
    TrafficPointElementVersion trafficPointElementVersionToUpdate = trafficPointElementService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));

    if (!trafficPointElementVersionToUpdate.getSloid().equals(trafficPointElementVersionModel.getSloid())) {
      String exceptionMessage = "Sloid for provided id: " + trafficPointElementVersionToUpdate.getSloid() +
          " and sloid in the request body: " + trafficPointElementVersionModel.getSloid() + " are not equal.";
      throw new SloidsNotEqualException(exceptionMessage);
    }

    update(trafficPointElementVersionToUpdate,
        TrafficPointElementVersionMapper.toEntity(trafficPointElementVersionModel));

    return getReadTrafficPointElementVersionModels(trafficPointElementVersionToUpdate);
  }

  @Override
  public List<ReadTrafficPointElementVersionModel> terminateTrafficPoint(String sloid,
      LocalDate validTo) {
    List<TrafficPointElementVersion> trafficPointElementVersions = trafficPointElementService.findBySloidOrderByValidFrom(sloid);
    if (trafficPointElementVersions.isEmpty()) {
      throw new SloidNotFoundException(sloid);
    }
    TrafficPointElementVersion lastTrafficPointElementVersion = trafficPointElementVersions.getLast();

    TrafficPointElementVersion updatingVersion = lastTrafficPointElementVersion.toBuilder().build();
    updatingVersion.setValidTo(validTo);
    terminate(lastTrafficPointElementVersion, updatingVersion);

    return getReadTrafficPointElementVersionModels(updatingVersion);
  }

  private List<ReadTrafficPointElementVersionModel> getReadTrafficPointElementVersionModels(
      TrafficPointElementVersion trafficPointElementVersion) {
    List<TrafficPointElementVersion> updatedTrafficPoint = trafficPointElementService.findBySloidOrderByValidFrom(
        trafficPointElementVersion.getSloid());
    servicePointDistributor.publishTrafficPointElements(updatedTrafficPoint);
    return updatedTrafficPoint
        .stream()
        .map(TrafficPointElementVersionMapper::toModel)
        .toList();
  }

  private TrafficPointElementVersion createTrafficPoint(TrafficPointElementVersion trafficPointElementVersion) {
    ServicePointNumber servicePointNumber = trafficPointElementVersion.getServicePointNumber();
    crossValidationService.validateServicePointNumberExists(servicePointNumber);
    geoReferenceService.addHeightToTrafficPoints(trafficPointElementVersion);
    return trafficPointElementService.create(trafficPointElementVersion,
        servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber));
  }

  private void update(TrafficPointElementVersion currentVersion, TrafficPointElementVersion editedVersion) {
    ServicePointNumber servicePointNumber = editedVersion.getServicePointNumber();
    crossValidationService.validateServicePointNumberExists(editedVersion.getServicePointNumber());
    List<ServicePointVersion> allServicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber);
    geoReferenceService.addHeightToTrafficPoints(editedVersion);
    trafficPointElementService.update(currentVersion, editedVersion, allServicePointVersions);
  }

  private void terminate(TrafficPointElementVersion currentVersion, TrafficPointElementVersion editedVersion) {
    ServicePointNumber servicePointNumber = currentVersion.getServicePointNumber();
    List<ServicePointVersion> allServicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber);
    DateRange dateRange = new DateRange(currentVersion.getValidFrom(), currentVersion.getValidTo());

    TerminationHelper.isValidToInLastVersionRange(currentVersion.getSloid(), dateRange, editedVersion.getValidTo());
    trafficPointElementService.update(currentVersion, editedVersion, allServicePointVersions);
  }
}
