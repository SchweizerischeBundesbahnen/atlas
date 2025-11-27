package ch.sbb.exportservice.job.lidi.ttfn.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportTimetableFieldNumberJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportTimetableFieldNumberJobService exportTimetableFieldNumberJobService = new ExportTimetableFieldNumberJobService(null,
        null, null);
    List<JobParams> exportTypes = exportTimetableFieldNumberJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE);
  }
}
