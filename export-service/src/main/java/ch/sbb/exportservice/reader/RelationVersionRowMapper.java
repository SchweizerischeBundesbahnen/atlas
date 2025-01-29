package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.exportservice.entity.RelationVersion;
import ch.sbb.exportservice.entity.RelationVersion.RelationVersionBuilder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;

public class RelationVersionRowMapper extends BaseRowMapper implements RowMapper<RelationVersion> {

  @Override
  public RelationVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
    RelationVersionBuilder<?, ?> builder = RelationVersion.builder();
    builder.id(rs.getLong("id"));
    builder.sloid(rs.getString("sloid"));
    builder.parentServicePointSloid(rs.getString("parent_service_point_sloid"));
    builder.parentServicePointNumber(ServicePointNumber.ofNumberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(rs.getInt("number"))));
    builder.referencePointSloid(rs.getString("reference_point_sloid"));
    builder.tactileVisualMarks(TactileVisualAttributeType.valueOf(rs.getString("tactile_visual_marks")));
    builder.contrastingAreas(StandardAttributeType.valueOf(rs.getString("contrasting_areas")));
    builder.stepFreeAccess(StepFreeAccessAttributeType.valueOf(rs.getString("step_free_access")));
    builder.referencePointElementType(ReferencePointElementType.valueOf(rs.getString("reference_point_element_type")));
    builder.validFrom(rs.getObject("valid_from", LocalDate.class));
    builder.validTo(rs.getObject("valid_to", LocalDate.class));
    builder.creationDate(rs.getObject("creation_date", LocalDateTime.class));
    builder.editionDate(rs.getObject("edition_date", LocalDateTime.class));
    builder.creator(rs.getString("creator"));
    builder.editor(rs.getString("editor"));
    builder.version(rs.getInt("version"));
    return builder.build();
  }


}
