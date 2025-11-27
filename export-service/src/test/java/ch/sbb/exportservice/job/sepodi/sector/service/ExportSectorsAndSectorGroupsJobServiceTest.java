package ch.sbb.exportservice.job.sepodi.sector.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportSectorsAndSectorGroupsJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportSectorsAndSectorGroupsJobService exportSectorsAndSectorGroupsJobService = new ExportSectorsAndSectorGroupsJobService(
        null, null);
    List<JobParams> exportTypes = exportSectorsAndSectorGroupsJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE);
  }

}
