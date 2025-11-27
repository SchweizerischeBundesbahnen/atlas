package ch.sbb.exportservice.job.sepodi.sectorgroup.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportSectorGroupJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportSectorGroupJobService exportSectorGroupJobService = new ExportSectorGroupJobService(null, null);
    List<JobParams> exportTypes = exportSectorGroupJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE);
  }

}
