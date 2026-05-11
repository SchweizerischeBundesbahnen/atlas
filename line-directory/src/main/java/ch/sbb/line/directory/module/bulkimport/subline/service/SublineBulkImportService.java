package ch.sbb.line.directory.module.bulkimport.subline.service;

import ch.sbb.atlas.api.lidi.SublineVersionModelV2;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.SublineUpdateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.module.subline.entity.SublineVersion;
import ch.sbb.line.directory.module.subline.service.SublineService;
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
public class SublineBulkImportService {

  private final SublineService sublineService;
  private final SublineApiClient sublineApiClient;

  @RunAsUser
  public void updateSublineByUsername(@RunAsUserParameter String username,
      BulkImportUpdateContainer<SublineUpdateCsvModel> bulkImportContainer) {
    log.info("Update versions in name of the user: {}", username);
    updateSubline(bulkImportContainer);
  }

  public void updateSubline(BulkImportUpdateContainer<SublineUpdateCsvModel> bulkImportUpdateContainer) {
    SublineUpdateCsvModel sublineUpdateCsvModel = bulkImportUpdateContainer.getObject();

    List<SublineVersion> currentPlatformVersions = getCurrentSublineVersions(sublineUpdateCsvModel);
    SublineVersion currentVersion = ImportUtils.getCurrentVersion(currentPlatformVersions,
        sublineUpdateCsvModel.getValidFrom(), sublineUpdateCsvModel.getValidTo());
    SublineVersionModelV2 updateModel = SublineBulkImportUpdate.apply(bulkImportUpdateContainer, currentVersion);

    sublineApiClient.updateSubline(currentVersion.getId(), updateModel);
  }

  private List<SublineVersion> getCurrentSublineVersions(SublineUpdateCsvModel sublineUpdateCsvModel) {
    if (sublineUpdateCsvModel.getSlnid() != null) {
      List<SublineVersion> sublineVersions = sublineService.findSubline(sublineUpdateCsvModel.getSlnid());
      if (sublineVersions.isEmpty()) {
        throw new SlnidNotFoundException(sublineUpdateCsvModel.getSlnid());
      }
      return sublineVersions;
    }
    throw new IllegalStateException("Slnid should be given");
  }

}

