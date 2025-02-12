package ch.sbb.exportservice.reader;

import ch.sbb.exportservice.entity.lidi.Line;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class LineRowMapper implements RowMapper<Line> {

  // todo
  @Override
  public Line mapRow(ResultSet rs, int rowNum) throws SQLException {
    return null;
  }

}
