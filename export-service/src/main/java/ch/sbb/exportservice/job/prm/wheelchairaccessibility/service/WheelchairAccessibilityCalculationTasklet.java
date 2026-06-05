package ch.sbb.exportservice.job.prm.wheelchairaccessibility.service;

import ch.sbb.atlas.wheelchairaccessibility.calculator.WheelchairAccessibility;
import ch.sbb.atlas.wheelchairaccessibility.model.Accessibility;
import ch.sbb.atlas.wheelchairaccessibility.model.Accessibility.AccessibilityInfo;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityFilter;
import ch.sbb.atlas.wheelchairaccessibility.model.AccessibilityRequest;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.platform.sql.PlatformVersionRowMapper;
import ch.sbb.exportservice.job.prm.platform.sql.PlatformVersionSqlQueryUtil;
import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.relation.sql.RelationVersionRowMapper;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import ch.sbb.exportservice.job.prm.stoppoint.sql.StopPointVersionRowMapper;
import ch.sbb.exportservice.job.prm.stoppoint.sql.StopPointVersionSqlQueryUtil;
import ch.sbb.exportservice.job.prm.wheelchairaccessibility.model.WheelchairAccessibilityCsvModel;
import ch.sbb.exportservice.job.prm.wheelchairaccessibility.writer.AccessibilityFileWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class WheelchairAccessibilityCalculationTasklet implements Tasklet {

  private final AccessibilityFileWriter accessibilityFileWriter;
  private final NamedParameterJdbcTemplate prmJdbcTemplate;

  public WheelchairAccessibilityCalculationTasklet(AccessibilityFileWriter accessibilityFileWriter,
      @Qualifier("prmJdbcTemplate") NamedParameterJdbcTemplate prmJdbcTemplate) {
    this.accessibilityFileWriter = accessibilityFileWriter;
    this.prmJdbcTemplate = prmJdbcTemplate;
  }

  @Override
  public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) {
    accessibilityFileWriter.open(chunkContext.getStepContext().getStepExecution().getExecutionContext());

    Map<String, List<StopPointVersion>> groupedStopPointVersions = prmJdbcTemplate.query(
            StopPointVersionSqlQueryUtil.SELECT_STATEMENT + StopPointVersionSqlQueryUtil.GROUP_BY_STATEMENT,
            new StopPointVersionRowMapper())
        .stream().collect(Collectors.groupingBy(StopPointVersion::getSloid));

    for (Map.Entry<String, List<StopPointVersion>> stopPoint : groupedStopPointVersions.entrySet()) {
      List<PlatformVersion> platformsOfStopPoint = getPlatformsOfStopPoint(stopPoint);
      List<RelationVersion> relationsOfStopPoint = getRelationsOfStopPoint(stopPoint);

      List<WheelchairAccessibilityCsvModel> accessibilityCsvModels = calculateAndMapToCsv(
          stopPoint, platformsOfStopPoint, relationsOfStopPoint);

      accessibilityFileWriter.write(accessibilityCsvModels);
    }

    accessibilityFileWriter.close();
    return RepeatStatus.FINISHED;
  }

  private static List<WheelchairAccessibilityCsvModel> calculateAndMapToCsv(
      Entry<String, List<StopPointVersion>> stopPoint, List<PlatformVersion> platformsOfStopPoint,
      List<RelationVersion> relationsOfStopPoint) {
    AccessibilityRequest accessibilityRequest = AccessibilityRequest.builder()
        .stopPoint(stopPoint.getValue())
        .platform(platformsOfStopPoint)
        .relations(relationsOfStopPoint)
        .build();

    Accessibility accessibility = WheelchairAccessibility.calculateStopPoint(accessibilityRequest,
        new AccessibilityFilter(LocalDate.now())).minify();

    return ToCsvMapper.builder()
        .sloid(stopPoint.getKey())
        .number(String.valueOf(stopPoint.getValue().getFirst().getNumber().getNumber()))
        .type("STOP_POINT").build().toModel(accessibility);
  }

  private List<PlatformVersion> getPlatformsOfStopPoint(Entry<String, List<StopPointVersion>> stopPoint) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("stopPointSloid", stopPoint.getKey());

    return prmJdbcTemplate.query(
        PlatformVersionSqlQueryUtil.SELECT_STATEMENT +
            " where parent_service_point_sloid=:stopPointSloid "
            + PlatformVersionSqlQueryUtil.GROUP_BY_STATEMENT,
        mapSqlParameterSource,
        new PlatformVersionRowMapper());
  }

  private List<RelationVersion> getRelationsOfStopPoint(Entry<String, List<StopPointVersion>> stopPoint) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("stopPointSloid", stopPoint.getKey());

    return prmJdbcTemplate.query(
        "select * from relation_version where parent_service_point_sloid=:stopPointSloid order by valid_from",
        mapSqlParameterSource,
        new RelationVersionRowMapper());
  }

  @Data
  @AllArgsConstructor
  @Builder
  static class ToCsvMapper {

    private String sloid;
    private String number;
    private String type;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    List<WheelchairAccessibilityCsvModel> toModel(Accessibility accessibility) {
      return accessibility.getAccessibilityInfos().stream().map(this::toModel).toList();
    }

    WheelchairAccessibilityCsvModel toModel(AccessibilityInfo accessibilityInfo) {
      return WheelchairAccessibilityCsvModel.builder()
          .number(number)
          .sloid(sloid)
          .type(type)
          .accessibility(accessibilityInfo.getAccessibilityState())
          .validFrom(dateTimeFormatter.format(accessibilityInfo.getDateRange().getFrom()))
          .validTo(dateTimeFormatter.format(accessibilityInfo.getDateRange().getTo()))
          .build();
    }
  }

}
