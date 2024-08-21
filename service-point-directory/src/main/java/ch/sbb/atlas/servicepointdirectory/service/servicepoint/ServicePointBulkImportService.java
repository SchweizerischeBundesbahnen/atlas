package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.bulk.ServicePointUpdateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
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
  private final ServicePointFotCommentService servicePointFotCommentService;

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
      return servicePointService.findAllByNumberOrderByValidFrom(
          ServicePointNumber.ofNumberWithoutCheckDigit(servicePointUpdate.getNumber()));
    } else if (servicePointUpdate.getSloid() != null) {
      return servicePointService.findBySloidAndOrderByValidFrom(servicePointUpdate.getSloid());
    }
    throw new IllegalStateException("Number or sloid should be given");
  }

}
