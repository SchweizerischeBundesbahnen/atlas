package ch.sbb.exportservice.job.prm.platform.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportPlatformJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportPlatformJobService exportPlatformJobService = new ExportPlatformJobService(null, null, null);
    List<JobParams> exportTypes = exportPlatformJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE_WITH_FUTURE);
  }

}
