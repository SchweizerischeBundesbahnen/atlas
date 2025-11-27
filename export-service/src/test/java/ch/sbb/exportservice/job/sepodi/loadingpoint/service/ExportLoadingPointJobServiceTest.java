package ch.sbb.exportservice.job.sepodi.loadingpoint.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportLoadingPointJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportLoadingPointJobService exportLoadingPointJobService = new ExportLoadingPointJobService(null, null, null);
    List<JobParams> exportTypes = exportLoadingPointJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.WORLD);
  }
}
