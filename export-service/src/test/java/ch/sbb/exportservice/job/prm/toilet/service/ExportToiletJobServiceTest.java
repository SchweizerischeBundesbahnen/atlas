package ch.sbb.exportservice.job.prm.toilet.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportToiletJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportToiletJobService exportToiletJobService = new ExportToiletJobService(null, null, null);
    List<JobParams> exportTypes = exportToiletJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE_WITH_FUTURE);
  }

}
