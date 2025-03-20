package ch.sbb.exportservice.job.lidi.subline.processor;

import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.exportservice.job.lidi.line.entity.Line;
import ch.sbb.exportservice.job.lidi.line.sql.LineRowMapper;
import ch.sbb.exportservice.job.lidi.subline.entity.Subline;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MainlineEnrichingProcessor {

  private final NamedParameterJdbcTemplate lidiJdbcTemplate;

  MainlineEnrichingProcessor(@Qualifier("lidiJdbcTemplate") NamedParameterJdbcTemplate lidiJdbcTemplate) {
    this.lidiJdbcTemplate = lidiJdbcTemplate;
  }

  public Subline addMainlinePropertiesToSubline(Subline subline) {
    Line prioritizedMainLine = prioritizedMainLine(subline);

    subline.setNumber(prioritizedMainLine.getNumber());
    subline.setSwissLineNumber(prioritizedMainLine.getSwissLineNumber());
    subline.setShortNumber(prioritizedMainLine.getShortNumber());
    subline.setOfferCategory(prioritizedMainLine.getOfferCategory());
    return subline;
  }

  private Line prioritizedMainLine(Subline subline) {
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("slnid", subline.getMainlineSlnid());

    List<Line> mainLine = lidiJdbcTemplate.query("select * from line_version where slnid=:slnid", mapSqlParameterSource,
        new LineRowMapper());

    return OverviewDisplayBuilder.getPrioritizedVersion(mainLine);
  }

}
// todo: trigger export for testing