package ch.sbb.exportservice.job.sepodi.sectorgroup.sql;

import ch.sbb.exportservice.job.sepodi.sectorgroup.entity.SectorGroupVersion;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SectorGroupVersionRowMapper implements RowMapper<SectorGroupVersion> {

  @Override
  public SectorGroupVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    return SectorGroupVersion.builder()
        .id(rs.getLong("id"))
        .sloid(rs.getString("sloid"))
        .validFrom(rs.getDate("valid_from").toLocalDate())
        .validTo(rs.getDate("valid_to").toLocalDate())
        .trafficPointSloid(rs.getString("traffic_point_sloid"))
        .designation(rs.getString("designation"))
        .creationDate(rs.getTimestamp("creation_date").toLocalDateTime())
        .creator(rs.getString("creator"))
        .editionDate(rs.getTimestamp("edition_date").toLocalDateTime())
        .editor(rs.getString("editor"))
        .version(rs.getInt("version"))
        .build();
  }
}
