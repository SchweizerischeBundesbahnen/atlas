package ch.sbb.exportservice.job.lidi.line.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportLineJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportLineJobService exportLineJobService = new ExportLineJobService(null, null, null);
    List<JobParams> exportTypes = exportLineJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE);
  }
}
