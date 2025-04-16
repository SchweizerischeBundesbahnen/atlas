package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.SublineApiInternal;
import ch.sbb.line.directory.service.SublineService;
import ch.sbb.line.directory.service.export.SublineVersionExportService;
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

  @Deprecated(forRemoval = true)
  @Override
  public List<URL> exportFullSublineVersions() {
    return sublineVersionExportService.exportFullVersions();
  }

  @Deprecated(forRemoval = true)
  @Override
  public List<URL> exportActualSublineVersions() {
    return sublineVersionExportService.exportActualVersions();
  }

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
