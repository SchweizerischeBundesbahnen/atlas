package ch.sbb.line.directory.service.bulk;

import ch.sbb.atlas.api.lidi.UpdateLineVersionModelV2;
import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.LineUpdateCsvModel;
import ch.sbb.atlas.imports.util.ImportUtils;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUser;
import ch.sbb.atlas.user.administration.security.aspect.RunAsUserParameter;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.exception.SlnidNotFoundException;
import ch.sbb.line.directory.service.LineService;
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
public class LineBulkImportService {

  private final LineService lineService;
  private final LineApiClient lineApiClient;

  @RunAsUser
  public void updateLineByUsername(@RunAsUserParameter String username,
      BulkImportUpdateContainer<LineUpdateCsvModel> bulkImportContainer) {
    log.info("Update versions in name of the user: {}", username);
    updateLine(bulkImportContainer);
  }

  public void updateLine(BulkImportUpdateContainer<LineUpdateCsvModel> bulkImportUpdateContainer) {
    LineUpdateCsvModel lineUpdateCsvModel = bulkImportUpdateContainer.getObject();

    List<LineVersion> currentPlatformVersions = getCurrentLineVersions(lineUpdateCsvModel);
    LineVersion currentVersion = ImportUtils.getCurrentVersion(currentPlatformVersions,
        lineUpdateCsvModel.getValidFrom(), lineUpdateCsvModel.getValidTo());
    UpdateLineVersionModelV2 updateModel = LineBulkImportUpdate.apply(bulkImportUpdateContainer, currentVersion);

    lineApiClient.updateLine(currentVersion.getId(), updateModel);
  }

  private List<LineVersion> getCurrentLineVersions(LineUpdateCsvModel lineUpdateCsvModel) {
    if (lineUpdateCsvModel.getSlnid() != null) {
      List<LineVersion> lineVersions = lineService.findLineVersions(lineUpdateCsvModel.getSlnid());
      if (lineVersions.isEmpty()) {
        throw new SlnidNotFoundException(lineUpdateCsvModel.getSlnid());
      }
      return lineVersions;
    }
    throw new IllegalStateException("Slnid should be given");
  }

}
