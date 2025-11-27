package ch.sbb.exportservice.job.prm.referencepoint.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportReferencePointJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportReferencePointJobService exportReferencePointJobService = new ExportReferencePointJobService(null, null, null);
    List<JobParams> exportTypes = exportReferencePointJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE_WITH_FUTURE);
  }

}
