package ch.sbb.exportservice.job.prm.relation.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportRelationJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportRelationJobService exportRelationJobService = new ExportRelationJobService(null, null, null);
    List<JobParams> exportTypes = exportRelationJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE_WITH_FUTURE);
  }

}
