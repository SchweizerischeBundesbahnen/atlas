package ch.sbb.exportservice.job.prm.parkinglot.service;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.job.BaseExportType;
import java.util.List;
import org.junit.jupiter.api.Test;

class ExportParkingLotJobServiceTest {

  @Test
  void shouldGetExportTypes() {
    ExportParkingLotJobService exportParkingLotJobService = new ExportParkingLotJobService(null, null, null);
    List<JobParams> exportTypes = exportParkingLotJobService.getExportTypes();
    assertThat(exportTypes).isNotNull().isEqualTo(BaseExportType.BASE_WITH_FUTURE);
  }
}
