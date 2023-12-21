package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.exportservice.entity.PlatformVersion.PlatformVersionBuilder;
import ch.sbb.exportservice.entity.PlatformVersion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.jdbc.core.RowMapper;

public class PlatformVersionRowMapper extends BaseRowMapper implements RowMapper<PlatformVersion> {

    @Override
    public PlatformVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
        PlatformVersionBuilder<?,?> builder = PlatformVersion.builder();
        builder.id(rs.getLong("id"));
        builder.sloid(rs.getString("sloid"));
        builder.parentSloidServicePoint(rs.getString("parent_service_point_sloid"));
        builder.boardingDevice(BoardingDeviceAttributeType.valueOf(rs.getString("boarding_device")));
        builder.adviceAccessInfo(rs.getString("advice_access_info"));
        builder.additionalInformation(rs.getString("additional_information"));
        builder.contrastingAreas(BooleanOptionalAttributeType.valueOf(rs.getString("contrasting_areas")));
        builder.dynamicAudio(BasicAttributeType.valueOf(rs.getString("dynamic_audio")));
        builder.dynamicVisual(BasicAttributeType.valueOf(rs.getString("dynamic_visual")));
        builder.height(rs.getObject("height") != null ? rs.getDouble("height") : null);
        builder.inclination(rs.getObject("inclination") != null ? rs.getDouble("inclination") : null);
        builder.inclinationLongitudinal(rs.getObject("inclination_longitudinal") != null ? rs.getDouble("inclination_longitudal") : null);
        builder.inclinationWidth(rs.getObject("inclination_width") != null ? rs.getDouble("inclination_width") : null);
        //builder.infoOpportunities();
        builder.levelAccessWheelchair(BasicAttributeType.valueOf(rs.getString("level_access_wheelchair")));
        builder.partialElevation(rs.getBoolean("partial_elevation"));
        builder.superElevation(rs.getObject("superelevation") != null ? rs.getDouble("superelevation") : null);
        builder.tactileSystems(BooleanOptionalAttributeType.valueOf(rs.getString("tactile_system")));
        builder.vehicleAccess(VehicleAccessAttributeType.valueOf(rs.getString("vehicle_access")));
        builder.validFrom(rs.getObject("valid_from", LocalDate.class));
        builder.validTo(rs.getObject("valid_to", LocalDate.class));
        builder.creationDate(rs.getObject("creation_date", LocalDateTime.class));
        builder.creator(rs.getString("creator"));
        builder.editionDate(rs.getObject("edition_date", LocalDateTime.class));
        builder.editor(rs.getString("editor"));
        return builder.build();
    }
}
