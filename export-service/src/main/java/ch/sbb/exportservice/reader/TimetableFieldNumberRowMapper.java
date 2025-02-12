package ch.sbb.exportservice.reader;

import ch.sbb.exportservice.entity.lidi.Line;
import ch.sbb.exportservice.entity.lidi.TimetableFieldNumber;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class TimetableFieldNumberRowMapper implements RowMapper<TimetableFieldNumber> {

  // todo
  @Override
  public TimetableFieldNumber mapRow(ResultSet rs, int rowNum) throws SQLException {
    return null;
  }

}
