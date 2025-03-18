package ch.sbb.exportservice.job.subline;

import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.exportservice.job.line.Line;
import ch.sbb.exportservice.job.line.LineRowMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class MainlineService {

  @Autowired
  @Qualifier("lidiJdbcTemplate")
  private NamedParameterJdbcTemplate lidiJdbcTemplate;

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

    List<Line> mainLine = lidiJdbcTemplate.query("select * from line_version where slnid=:slnid", mapSqlParameterSource, new LineRowMapper());

    return OverviewDisplayBuilder.getPrioritizedVersion(mainLine);
  }
}
