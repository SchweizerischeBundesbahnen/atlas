package ch.sbb.exportservice.job.sepodi.sector.sql;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.exportservice.job.sepodi.sector.entity.SectorWithGroupVersion;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class SectorWithGroupVersionRowMapper implements RowMapper<SectorWithGroupVersion> {

  @Override
  public SectorWithGroupVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    return SectorWithGroupVersion.builder()
        .sloid(rs.getString("sloid"))
        .type(rs.getString("type"))
        .trafficPointSloid(rs.getString("traffic_point_sloid"))
        .validFrom(rs.getDate("valid_from").toLocalDate())
        .validTo(rs.getDate("valid_to").toLocalDate())
        .designation(rs.getString("designation"))
        .length(rs.getDouble("length"))
        .edgeHeight(rs.getDouble("edge_height"))
        .east(rs.getDouble("east"))
        .north(rs.getDouble("north"))
        .height(rs.getDouble("height"))
        .spatialReference(rs.getString("spatial_reference") == null ? null : SpatialReference.valueOf(rs.getString(
            "spatial_reference")))
        .relatedGroups(rs.getString("related_groups"))
        .relatedSectors(rs.getString("related_sectors"))
        .creationDate(rs.getTimestamp("creation_date").toLocalDateTime())
        .editionDate(rs.getTimestamp("edition_date").toLocalDateTime())
        .build();
  }
}
