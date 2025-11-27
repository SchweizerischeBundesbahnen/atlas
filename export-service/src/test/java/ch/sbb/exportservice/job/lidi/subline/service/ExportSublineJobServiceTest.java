package ch.sbb.exportservice.job.lidi.subline.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportSublineJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportSublineJobService exportSublineJobService = new ExportSublineJobService(null, null, null);
    List<JobParams> exportTypes = exportSublineJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE);
  }

}
