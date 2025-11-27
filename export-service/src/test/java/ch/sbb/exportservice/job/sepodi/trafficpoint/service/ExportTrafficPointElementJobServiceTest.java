package ch.sbb.exportservice.job.sepodi.trafficpoint.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportTrafficPointElementJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportTrafficPointElementJobService exportTrafficPointElementJobService = new ExportTrafficPointElementJobService(null, null,
        null);
    List<JobParams> exportTypes = exportTrafficPointElementJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.WORLD);
  }

}
