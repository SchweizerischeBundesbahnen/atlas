package ch.sbb.exportservice.job.sepodi.sector.sql;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.model.Status;
import ch.sbb.exportservice.job.sepodi.sector.entity.SectorVersion;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SectorVersionRowMapper implements RowMapper<SectorVersion> {

  @Override
  public SectorVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    return SectorVersion.builder()
        .id(rs.getLong("id"))
        .sloid(rs.getString("sloid"))
        .status(Status.valueOf(rs.getString("status")))
        .validFrom(rs.getDate("valid_from").toLocalDate())
        .validTo(rs.getDate("valid_to").toLocalDate())
        .east(rs.getDouble("east"))
        .north(rs.getDouble("north"))
        .height(rs.getDouble("height"))
        .length(rs.getDouble("length"))
        .edgeHeight(rs.getDouble("edge_height"))
        .spatialReference(SpatialReference.valueOf(rs.getString("spatial_reference")))
        .trafficPointSloid(rs.getString("traffic_point_sloid"))
        .designation(rs.getString("designation"))
        .length(rs.getDouble("length"))
        .creationDate(rs.getTimestamp("creation_date").toLocalDateTime())
        .creator(rs.getString("creator"))
        .editionDate(rs.getTimestamp("edition_date").toLocalDateTime())
        .editor(rs.getString("editor"))
        .version(rs.getInt("version"))
        .version(rs.getInt("version"))
        .build();
  }
}
