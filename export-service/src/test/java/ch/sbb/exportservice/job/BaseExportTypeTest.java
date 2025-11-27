package ch.sbb.exportservice.job;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.model.ExportTypeV2;
import org.junit.jupiter.api.Test;

class BaseExportTypeTest {

  @Test
  void shouldContainBase() {
    assertThat(BaseExportType.BASE)
        .extracting(JobParams::getExportTypeV2)
        .containsExactly(ExportTypeV2.FULL, ExportTypeV2.ACTUAL, ExportTypeV2.TIMETABLE_YEARS);
  }

  @Test
  @Deprecated(forRemoval = true)
  void shouldContainBaseFuture() {
    assertThat(BaseExportType.BASE_WITH_FUTURE)
        .extracting(JobParams::getExportTypeV2)
        .containsExactly(ExportTypeV2.FULL, ExportTypeV2.ACTUAL, ExportTypeV2.TIMETABLE_YEARS, ExportTypeV2.FUTURE_TIMETABLE);
  }

  @Test
  void shouldContainWorld() {
    assertThat(BaseExportType.WORLD)
        .extracting(JobParams::getExportTypeV2)
        .containsExactly(ExportTypeV2.WORLD_FULL, ExportTypeV2.WORLD_ACTUAL, ExportTypeV2.WORLD_FUTURE_TIMETABLE,
            ExportTypeV2.WORLD_TIMETABLE_YEARS);
  }

  @Test
  void shouldContainSwiss() {
    assertThat(BaseExportType.SWISS)
        .extracting(JobParams::getExportTypeV2)
        .containsExactly(ExportTypeV2.SWISS_FULL, ExportTypeV2.SWISS_ACTUAL, ExportTypeV2.SWISS_FUTURE_TIMETABLE,
            ExportTypeV2.SWISS_TIMETABLE_YEARS);
  }

  @Test
  void shouldContainSwissWorld() {
    assertThat(BaseExportType.SWISS_WORLD)
        .extracting(JobParams::getExportTypeV2)
        .containsExactly(ExportTypeV2.WORLD_FULL, ExportTypeV2.WORLD_ACTUAL, ExportTypeV2.WORLD_FUTURE_TIMETABLE,
            ExportTypeV2.WORLD_TIMETABLE_YEARS, ExportTypeV2.SWISS_FULL, ExportTypeV2.SWISS_ACTUAL,
            ExportTypeV2.SWISS_FUTURE_TIMETABLE,
            ExportTypeV2.SWISS_TIMETABLE_YEARS);
  }
}
