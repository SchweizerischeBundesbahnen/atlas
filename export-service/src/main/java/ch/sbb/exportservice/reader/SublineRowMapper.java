package ch.sbb.exportservice.reader;

import ch.sbb.exportservice.entity.lidi.Subline;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SublineRowMapper implements RowMapper<Subline> {

  // todo
  @Override
  public Subline mapRow(ResultSet rs, int rowNum) throws SQLException {
    return null;
  }

}
