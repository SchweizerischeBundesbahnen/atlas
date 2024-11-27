package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointBulkImportService {

  private final ServicePointService servicePointService;
  private final ServicePointApiClient servicePointApiClient;

  @RunAsUser
  public void updateServicePointByUserName(@RunAsUserParameter String userName, BulkImportUpdateContainer<ServicePointUpdateCsvModel> bulkImportContainer) {
    log.info("Update versions in name of the user: {}", userName);
    updateServicePoint(bulkImportContainer);
  }

  public void updateServicePoint(BulkImportUpdateContainer<ServicePointUpdateCsvModel> bulkImportContainer) {
    ServicePointUpdateCsvModel servicePointUpdate = bulkImportContainer.getObject();

    List<ServicePointVersion> currentVersions = getCurrentVersions(servicePointUpdate);
    ServicePointVersion currentVersion = ImportUtils.getCurrentVersion(currentVersions,
        servicePointUpdate.getValidFrom(), servicePointUpdate.getValidTo());

    UpdateServicePointVersionModel updateModel = ServicePointBulkImportUpdate.apply(bulkImportContainer, currentVersion);

    servicePointApiClient.updateServicePoint(currentVersion.getId(), updateModel);
  }

  @RunAsUser
  public void createServicePointByUserName(@RunAsUserParameter String userName, BulkImportUpdateContainer<ServicePointCreateCsvModel> bulkImportContainer) {
    log.info("Update versions in name of the user: {}", userName);
    createServicePoint(bulkImportContainer);
  }

  public void createServicePoint(BulkImportUpdateContainer<ServicePointCreateCsvModel> bulkImportContainer) {
    CreateServicePointVersionModel createModel = ServicePointBulkImportCreate.apply(bulkImportContainer);
    servicePointApiClient.createServicePoint(createModel);

  }

  private List<ServicePointVersion> getCurrentVersions(ServicePointUpdateCsvModel servicePointUpdate) {
    if (servicePointUpdate.getNumber() != null) {
      ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(servicePointUpdate.getNumber());
      List<ServicePointVersion> servicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber);
      if (servicePointVersions.isEmpty()) {
        throw new ServicePointNumberNotFoundException(servicePointNumber);
      }
      return servicePointVersions;
    } else if (servicePointUpdate.getSloid() != null) {
      List<ServicePointVersion> servicePointVersions = servicePointService.findBySloidAndOrderByValidFrom(
          servicePointUpdate.getSloid());
      if (servicePointVersions.isEmpty()) {
        throw new SloidNotFoundException(servicePointUpdate.getSloid());
      }
      return servicePointVersions;
    }
    throw new IllegalStateException("Number or sloid should be given");
  }

}
