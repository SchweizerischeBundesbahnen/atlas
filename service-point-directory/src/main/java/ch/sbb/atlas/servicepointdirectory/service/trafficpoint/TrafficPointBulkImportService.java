package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TrafficPointBulkImportService {

  private final TrafficPointElementService trafficPointElementService;
  private final ServicePointService servicePointService;

  @RunAsUser
  public void updateTrafficPointByUserName(@RunAsUserParameter String userName,
      BulkImportUpdateContainer<TrafficPointUpdateCsvModel> bulkImportContainer) {
    log.info("Update versions in name of the user: {}", userName);
    updateTrafficPoint(bulkImportContainer);
  }

  public void updateTrafficPoint(BulkImportUpdateContainer<TrafficPointUpdateCsvModel> bulkImportContainer) {
    TrafficPointUpdateCsvModel trafficPointUpdateCsvModel = bulkImportContainer.getObject();

    List<TrafficPointElementVersion> currentTrafficPointVersions = getCurrentTrafficPointVersions(trafficPointUpdateCsvModel);
    TrafficPointElementVersion currentVersion = ImportUtils.getCurrentVersion(currentTrafficPointVersions,
        trafficPointUpdateCsvModel.getValidFrom(), trafficPointUpdateCsvModel.getValidTo());

    List<ServicePointVersion> currentServicePointVersions =
        getCurrentServicePointVersions(currentVersion.getServicePointNumber());

    TrafficPointElementVersion editedVersion = TrafficPointBulkImportUpdate
        .applyUpdateFromCsv(currentVersion, trafficPointUpdateCsvModel);
    TrafficPointBulkImportUpdate.applyNulling(bulkImportContainer.getAttributesToNull(), editedVersion);

    trafficPointElementService.update(currentVersion, editedVersion, currentServicePointVersions);
  }

  private List<TrafficPointElementVersion> getCurrentTrafficPointVersions(TrafficPointUpdateCsvModel trafficPointUpdateCsvModel) {
    if (trafficPointUpdateCsvModel.getSloid() != null) {
      List<TrafficPointElementVersion> servicePointVersions =
          trafficPointElementService.findBySloidOrderByValidFrom(trafficPointUpdateCsvModel.getSloid());
      if (servicePointVersions.isEmpty()) {
        throw new SloidNotFoundException(trafficPointUpdateCsvModel.getSloid());
      }
      return servicePointVersions;
    }
    throw new IllegalStateException("Sloid should be given");
  }

  private List<ServicePointVersion> getCurrentServicePointVersions(ServicePointNumber servicePointNumber) {
    List<ServicePointVersion> servicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber);
    if (servicePointVersions.isEmpty()) {
      throw new ServicePointNumberNotFoundException(servicePointNumber);
    }
    return servicePointVersions;
  }

}
