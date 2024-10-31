package ch.sbb.atlas.servicepointdirectory.service.trafficpoint.bulk;

import ch.sbb.atlas.api.servicepoint.CreateTrafficPointElementVersionModel;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.TrafficPointUpdateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
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
public class TrafficPointElementBulkImportService {

  private final TrafficPointElementService trafficPointElementService;
  private final TrafficPointElementApiClient trafficPointElementApiClient;

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

    CreateTrafficPointElementVersionModel updateModel = TrafficPointElementBulkImportUpdate
        .applyUpdateFromCsv(currentVersion, trafficPointUpdateCsvModel);
    TrafficPointElementBulkImportUpdate.applyNulling(bulkImportContainer.getAttributesToNull(), updateModel);

    trafficPointElementApiClient.updateServicePoint(currentVersion.getId(), updateModel);
  }

  private List<TrafficPointElementVersion> getCurrentTrafficPointVersions(TrafficPointUpdateCsvModel trafficPointUpdateCsvModel) {
    if (trafficPointUpdateCsvModel.getSloid() != null) {
      List<TrafficPointElementVersion> trafficPointElementVersions =
          trafficPointElementService.findBySloidOrderByValidFrom(trafficPointUpdateCsvModel.getSloid());
      if (trafficPointElementVersions.isEmpty()) {
        throw new SloidNotFoundException(trafficPointUpdateCsvModel.getSloid());
      }
      return trafficPointElementVersions;
    }
    throw new IllegalStateException("Sloid should be given");
  }

}
