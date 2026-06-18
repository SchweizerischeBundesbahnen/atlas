package ch.sbb.prm.directory.module.bulkimport.service;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.terminate.SloidTerminateCsvModel;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
import ch.sbb.prm.directory.module.bulkimport.client.PlatformApiClient;
import ch.sbb.prm.directory.module.bulkimport.client.StopPointApiClient;
import ch.sbb.prm.directory.module.platform.service.PlatformService;
import ch.sbb.prm.directory.module.stoppoint.service.StopPointService;
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
public class StopPointBulkImportService {

  private final StopPointService stopPointService;
  private final PlatformService platformService;
  private final PlatformApiClient platformApiClient;
  private final StopPointApiClient stopPointApiClient;

  @RunAsUser
  public void terminateStopPointByUsername(@RunAsUserParameter String userName,
      BulkImportUpdateContainer<SloidTerminateCsvModel> bulkImportContainer) {
    log.info("Terminating versions in name of the user: {}", userName);
    terminateStopPoint(bulkImportContainer);
  }

  public void terminateStopPoint(BulkImportUpdateContainer<SloidTerminateCsvModel> bulkImportContainer) {
    stopPointApiClient.terminateStopPoint(bulkImportContainer.getObject().getSloid(),
        bulkImportContainer.getObject().getValidTo());
  }

}
