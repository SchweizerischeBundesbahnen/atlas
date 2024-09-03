package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.exception.ServicePointNumberNotFoundException;
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
public class ServicePointBulkImportService {

  private final ServicePointService servicePointService;

  public void updateServicePoint(BulkImportUpdateContainer<ServicePointUpdateCsvModel> bulkImportContainer) {
    ServicePointUpdateCsvModel servicePointUpdate = bulkImportContainer.getObject();

    List<ServicePointVersion> currentVersions = getCurrentVersions(servicePointUpdate);
    ServicePointVersion currentVersion = ImportUtils.getCurrentVersion(currentVersions,
        servicePointUpdate.getValidFrom(), servicePointUpdate.getValidTo());

    ServicePointVersion editedVersion = ServicePointBulkImportUpdate.applyUpdateFromCsv(currentVersion, servicePointUpdate);
    ServicePointBulkImportUpdate.applyNulling(bulkImportContainer.getAttributesToNull(), editedVersion);

    servicePointService.update(currentVersion, editedVersion, currentVersions);
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
