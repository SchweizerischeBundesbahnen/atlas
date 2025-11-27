package ch.sbb.exportservice.job.prm.contactpoint.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportContactPointJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportContactPointJobService exportContactPointJobService = new ExportContactPointJobService(null, null, null);
    List<JobParams> exportTypes = exportContactPointJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE_WITH_FUTURE);
  }

}
