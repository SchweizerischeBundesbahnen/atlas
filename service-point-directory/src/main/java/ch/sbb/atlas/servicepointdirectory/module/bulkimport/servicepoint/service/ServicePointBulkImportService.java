package ch.sbb.atlas.servicepointdirectory.module.bulkimport.servicepoint.service;

import ch.sbb.atlas.api.servicepoint.CreateServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.api.servicepoint.TerminateServicePointModel;
import ch.sbb.atlas.api.servicepoint.UpdateServicePointVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointGlobalIdUpdateCsvModel;
import ch.sbb.atlas.imports.model.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.model.create.ServicePointCreateCsvModel;
import ch.sbb.atlas.imports.model.terminate.ServicePointTerminateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.bulkimport.servicepoint.exception.GlobalIdBulkImportInfoException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.InvalidGlobalIdException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointIdentifierMismatchException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.model.GlobalId;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.GlobalIdService;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointService;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServicePointBulkImportService {

  private final ServicePointService servicePointService;
  private final ServicePointApiClient servicePointApiClient;
  private final GlobalIdService globalIdService;

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
    log.info("Terminate versions in name of the user: {}", userName);
    terminateServicePoint(bulkImportContainer);
  }

  public void terminateServicePoint(BulkImportUpdateContainer<ServicePointTerminateCsvModel> bulkImportContainer) {
    ServicePointTerminateCsvModel servicePointTerminate = bulkImportContainer.getObject();

    List<ServicePointVersion> currentVersions = getCurrentVersions(servicePointTerminate.getSloid(),
        servicePointTerminate.getNumber());

    TerminateServicePointModel updateModel = ServicePointBulkImportTerminate.apply(bulkImportContainer,
        currentVersions.getLast());

    servicePointApiClient.terminateServicePoint(currentVersions.getLast().getId(), updateModel);
  }

  @RunAsUser
  public void updateGlobalIdByUserName(@RunAsUserParameter String userName,
      BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> bulkImportContainer) {
    log.info("Update global id in name of the user: {}", userName);
    updateGlobalId(bulkImportContainer);
  }

  public void updateGlobalId(BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> bulkImportContainer) {
    ServicePointGlobalIdUpdateCsvModel globalIdUpdate = bulkImportContainer.getObject();

    List<ServicePointVersion> currentVersions = getCurrentVersions(globalIdUpdate.getSloid(), globalIdUpdate.getNumber());
    ServicePointVersion currentVersion = currentVersions.getLast();
    verifyIdentifiersDescribeTheSameServicePoint(globalIdUpdate, currentVersion);

    ServicePointNumber servicePointNumber = currentVersion.getNumber();

    if (isRemoval(bulkImportContainer, globalIdUpdate)) {
      globalIdService.removeAndGet(servicePointNumber).ifPresent(removedGlobalId -> {
        throw GlobalIdBulkImportInfoException.removed(removedGlobalId);
      });
      return;
    }

    GlobalId globalId = GlobalId.of(globalIdUpdate.getGlobalId(), servicePointNumber.getCountry());
    repointWithRetry(servicePointNumber, globalId).ifPresent(displaced -> {
      throw GlobalIdBulkImportInfoException.repointed(globalId.value(), displaced);
    });
  }

  private boolean isRemoval(BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> bulkImportContainer,
      ServicePointGlobalIdUpdateCsvModel globalIdUpdate) {
    boolean explicitlyNulled = bulkImportContainer.getAttributesToNull()
        .contains(ServicePointGlobalIdUpdateCsvModel.Fields.globalId);
    if (explicitlyNulled) {
      return true;
    }
    if (StringUtils.isBlank(globalIdUpdate.getGlobalId())) {
      throw InvalidGlobalIdException.empty();
    }
    return false;
  }

  private void verifyIdentifiersDescribeTheSameServicePoint(ServicePointGlobalIdUpdateCsvModel globalIdUpdate,
      ServicePointVersion currentVersion) {
    if (StringUtils.isNotBlank(globalIdUpdate.getSloid()) && globalIdUpdate.getNumber() != null
        && !Objects.equals(globalIdUpdate.getSloid(), currentVersion.getSloid())) {
      throw new ServicePointIdentifierMismatchException(globalIdUpdate.getSloid(), globalIdUpdate.getNumber());
    }
  }

  private Optional<ServicePointNumber> repointWithRetry(ServicePointNumber servicePointNumber, GlobalId globalId) {
    try {
      return globalIdService.saveWithRepoint(servicePointNumber, globalId);
    } catch (DataIntegrityViolationException | ConcurrencyFailureException exception) {
      log.info("Retrying Global-ID {} for service point {} after a concurrent modification", globalId.value(),
          servicePointNumber, exception);
      return globalIdService.saveWithRepoint(servicePointNumber, globalId);
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
