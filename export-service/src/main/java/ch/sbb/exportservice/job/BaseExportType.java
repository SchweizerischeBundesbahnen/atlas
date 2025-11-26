package ch.sbb.exportservice.job;

import ch.sbb.exportservice.job.BaseExportJobService.JobParams;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class BaseExportType {

  private static final List<JobParams> BASE = List.of(
      new JobParams(ExportTypeV2.FULL),
      new JobParams(ExportTypeV2.ACTUAL),
      new JobParams(ExportTypeV2.TIMETABLE_YEARS)
  );

  private static final List<JobParams> WORLD = List.of(
      new JobParams(ExportTypeV2.WORLD_FULL),
      new JobParams(ExportTypeV2.WORLD_ACTUAL),
      new JobParams(ExportTypeV2.WORLD_FUTURE_TIMETABLE),
      new JobParams(ExportTypeV2.WORLD_TIMETABLE_YEARS)
  );

  private static final List<JobParams> SWISS = List.of(
      new JobParams(ExportTypeV2.SWISS_FULL),
      new JobParams(ExportTypeV2.SWISS_ACTUAL),
      new JobParams(ExportTypeV2.SWISS_FUTURE_TIMETABLE),
      new JobParams(ExportTypeV2.SWISS_TIMETABLE_YEARS)
  );

  private static final List<JobParams> SWISS_WORLD = Stream.concat(
      WORLD.stream(), SWISS.stream()).toList();

  @Deprecated(forRemoval = true)
  private static final List<JobParams> BASE_WITH_FUTURE = Stream.concat(
      BASE.stream(), Stream.of(new JobParams(ExportTypeV2.FUTURE_TIMETABLE))).toList();

  @Deprecated(forRemoval = true)
  public static List<JobParams> getBaseExportTypesWithFutureTimetable() {
    return BASE_WITH_FUTURE;
  }

  public static List<JobParams> getBaseExportTypes() {
    return BASE;
  }

  public static List<JobParams> getWorldExportTypes() {
    return WORLD;
  }

  public static List<JobParams> getSwissAndWorldExportTypes() {
    return SWISS_WORLD;
  }
}
