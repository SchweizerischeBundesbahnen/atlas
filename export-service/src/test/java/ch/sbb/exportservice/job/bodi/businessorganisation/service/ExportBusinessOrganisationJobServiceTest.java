package ch.sbb.exportservice.job.bodi.businessorganisation.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportBusinessOrganisationJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportBusinessOrganisationJobService exportBusinessOrganisationJobService = new ExportBusinessOrganisationJobService(
        null,
        null,
        null
    );
    List<JobParams> exportTypes = exportBusinessOrganisationJobService.getExportTypes();

    assertThat(exportTypes)
        .isNotNull()
        .isEqualTo(BaseExportType.BASE_WITH_FUTURE);
  }
}
