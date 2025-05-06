package ch.sbb.atlas.servicepointdirectory.service.servicepoint.bulk;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.TerminateServicePointModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.atlas.imports.model.terminate.ServicePointTerminateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.exception.TerminationNotAllowedValidToNotWithinLastVersionRangeException;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
import java.time.LocalDate;
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
  public void updateServicePointByUserName(@RunAsUserParameter String userName,
      BulkImportUpdateContainer<ServicePointUpdateCsvModel> bulkImportContainer) {
    log.info("Update versions in name of the user: {}", userName);
    updateServicePoint(bulkImportContainer);
  }

  public void updateServicePoint(BulkImportUpdateContainer<ServicePointUpdateCsvModel> bulkImportContainer) {
    ServicePointUpdateCsvModel servicePointUpdate = bulkImportContainer.getObject();

    List<ServicePointVersion> currentVersions = getCurrentVersions(servicePointUpdate.getSloid(), servicePointUpdate.getNumber());
    ServicePointVersion currentVersion = ImportUtils.getCurrentVersion(currentVersions,
        servicePointUpdate.getValidFrom(), servicePointUpdate.getValidTo());

    UpdateServicePointVersionModel updateModel = ServicePointBulkImportUpdate.apply(bulkImportContainer, currentVersion);

    servicePointApiClient.updateServicePoint(currentVersion.getId(), updateModel);
  }

  @RunAsUser
  public ReadServicePointVersionModel createServicePointByUserName(@RunAsUserParameter String userName,
      BulkImportUpdateContainer<ServicePointCreateCsvModel> bulkImportContainer) {
    log.info("Create versions in name of the user: {}", userName);
    return createServicePoint(bulkImportContainer);
  }

  public ReadServicePointVersionModel createServicePoint(
      BulkImportUpdateContainer<ServicePointCreateCsvModel> bulkImportContainer) {
    CreateServicePointVersionModel createModel = ServicePointBulkImportCreate.apply(bulkImportContainer);
    return servicePointApiClient.createServicePoint(createModel);
  }

  @RunAsUser
  public void terminateServicePointByUserName(@RunAsUserParameter String userName,
      BulkImportUpdateContainer<ServicePointTerminateCsvModel> bulkImportContainer) {
    log.info("Update versions in name of the user: {}", userName);
    terminateServicePoint(bulkImportContainer);
  }

  public void terminateServicePoint(BulkImportUpdateContainer<ServicePointTerminateCsvModel> bulkImportContainer) {
    ServicePointTerminateCsvModel servicePointTerminate = bulkImportContainer.getObject();

    List<ServicePointVersion> currentVersions = getCurrentVersions(servicePointTerminate.getSloid(),
        servicePointTerminate.getNumber());

    TerminateServicePointModel updateModel = ServicePointBulkImportTerminate.apply(bulkImportContainer,
        currentVersions.getLast());

    validateTerminateValidToInRange(servicePointTerminate, currentVersions.getLast());
    servicePointApiClient.terminateServicePoint(currentVersions.getLast().getId(), updateModel);
  }

  private void validateTerminateValidToInRange(ServicePointTerminateCsvModel servicePointTerminateCsvModel,
      ServicePointVersion currentVersion) {
    LocalDate terminateValidTo = servicePointTerminateCsvModel.getValidTo();
    LocalDate currentValidFrom = currentVersion.getValidFrom();
    LocalDate currentValidTo = currentVersion.getValidTo();

    if (!currentValidTo.isAfter(terminateValidTo) || terminateValidTo.isBefore(currentValidFrom)) {
      throw new TerminationNotAllowedValidToNotWithinLastVersionRangeException(
          servicePointTerminateCsvModel.getSloid(),
          terminateValidTo,
          currentValidFrom,
          currentValidTo);
    }
  }

  private List<ServicePointVersion> getCurrentVersions(String sloid,
      Integer number) {
    if (number != null) {
      ServicePointNumber servicePointNumber = ServicePointNumber.ofNumberWithoutCheckDigit(number);
      List<ServicePointVersion> servicePointVersions = servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber);
      if (servicePointVersions.isEmpty()) {
        throw new ServicePointNumberNotFoundException(servicePointNumber);
      }
      return servicePointVersions;
    } else if (sloid != null) {
      List<ServicePointVersion> servicePointVersions = servicePointService.findBySloidAndOrderByValidFrom(
          sloid);
      if (servicePointVersions.isEmpty()) {
        throw new SloidNotFoundException(sloid);
      }
      return servicePointVersions;
    }
    throw new IllegalStateException("Number or sloid should be given");
  }

}
