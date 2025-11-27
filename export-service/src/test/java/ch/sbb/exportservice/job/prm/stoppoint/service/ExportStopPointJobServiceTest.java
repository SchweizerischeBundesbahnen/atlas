package ch.sbb.exportservice.job.prm.stoppoint.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportStopPointJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportStopPointJobService exportStopPointJobService = new ExportStopPointJobService(null, null, null);
    List<JobParams> exportTypes = exportStopPointJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE_WITH_FUTURE);
  }
}
