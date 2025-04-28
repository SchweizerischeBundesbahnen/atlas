package ch.sbb.exportservice.job.prm.recording.obligation.sql;

import ch.sbb.exportservice.job.prm.recording.obligation.entity.RecordingObligation;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class RecordingObligationRowMapper implements RowMapper<RecordingObligation> {

  @Override
  public RecordingObligation mapRow(ResultSet rs, int rowNum) throws SQLException {
    return RecordingObligation.builder()
        .sloid(rs.getString("sloid"))
        .recordingObligation(rs.getBoolean("recording_obligation"))
        .build();
  }

}
