package ch.sbb.line.directory.module.subline.controller;

import ch.sbb.atlas.api.lidi.SublineApiInternal;
import ch.sbb.line.directory.module.subline.export.SublineVersionExportService;
import ch.sbb.line.directory.module.subline.service.SublineService;
import java.net.URL;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SublineControllerInternal implements SublineApiInternal {

  private final SublineService sublineService;
  private final SublineVersionExportService sublineVersionExportService;

  @Override
  public void revokeSubline(String slnid) {
    sublineService.revokeSubline(slnid);
  }

  /**
   * @deprecated since V2.544.0
   */
  @Deprecated(forRemoval = true)
  @Override
  public List<URL> exportFullSublineVersions() {
    return sublineVersionExportService.exportFullVersions();
  }

  /**
   * @deprecated since V2.544.0
   */
  @Deprecated(forRemoval = true)
  @Override
  public List<URL> exportActualSublineVersions() {
    return sublineVersionExportService.exportActualVersions();
  }

  /**
   * @deprecated since V2.544.0
   */
  @Deprecated(forRemoval = true)
  @Override
  public List<URL> exportFutureTimetableSublineVersions() {
    return sublineVersionExportService.exportFutureTimetableVersions();
  }

  @Override
  public void deleteSublines(String slnid) {
    sublineService.deleteAll(slnid);
  }

}
