package ch.sbb.exportservice.reader;

import ch.sbb.atlas.api.prm.enumeration.*;
import ch.sbb.exportservice.entity.PlatformVersion.PlatformVersionBuilder;
import ch.sbb.exportservice.entity.PlatformVersion;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;

public class PlatformVersionRowMapper extends BaseRowMapper implements RowMapper<PlatformVersion> {

    @Override
    public PlatformVersion mapRow(ResultSet rs, int rowNum) throws SQLException {
        PlatformVersionBuilder<?,?> builder = PlatformVersion.builder();
        builder.id(rs.getLong("id"));
        builder.sloid(rs.getString("sloid"));
        builder.parentSloidServicePoint(rs.getString("parent_service_point_sloid"));
        builder.boardingDevice(
                rs.getObject("boarding_device") != null ? BoardingDeviceAttributeType.valueOf(rs.getString("boarding_device")) : null);
        builder.adviceAccessInfo(rs.getString("advice_access_info"));
        builder.additionalInformation(rs.getString("additional_information"));
        builder.contrastingAreas(
                rs.getObject("contrasting_areas") != null ? BooleanOptionalAttributeType.valueOf(rs.getString("contrasting_areas")) : null);
        builder.dynamicAudio(
                rs.getObject("dynamic_audio") != null ? BasicAttributeType.valueOf(rs.getString("dynamic_audio")) : null );
        builder.dynamicVisual(
                rs.getObject("dynamic_visual") != null ? BasicAttributeType.valueOf(rs.getString("dynamic_visual")) : null);
        builder.height(rs.getObject("height") != null ? rs.getDouble("height") : null);
        builder.inclination(rs.getObject("inclination") != null ? rs.getDouble("inclination") : null);
        builder.inclinationLongitudinal(rs.getObject("inclination_longitudinal") != null ? rs.getDouble("inclination_longitudinal") : null);
        builder.inclinationWidth(rs.getObject("inclination_width") != null ? rs.getDouble("inclination_width") : null);
        setInfoOpportunities(builder, rs.getString("info_opportunities"));
        builder.levelAccessWheelchair(
                rs.getObject("level_access_wheelchair") != null ? BasicAttributeType.valueOf(rs.getString("level_access_wheelchair")) : null);
        builder.partialElevation(rs.getBoolean("partial_elevation"));
        builder.superElevation(rs.getObject("superelevation") != null ? rs.getDouble("superelevation") : null);
        builder.tactileSystems(
                rs.getObject("tactile_system") != null ? BooleanOptionalAttributeType.valueOf(rs.getString("tactile_system")) : null);
        builder.vehicleAccess(
                rs.getObject("vehicle_access") != null ? VehicleAccessAttributeType.valueOf(rs.getString("vehicle_access")) : null);
        builder.validFrom(rs.getObject("valid_from", LocalDate.class));
        builder.validTo(rs.getObject("valid_to", LocalDate.class));
        builder.creationDate(rs.getObject("creation_date", LocalDateTime.class));
        builder.creator(rs.getString("creator"));
        builder.editionDate(rs.getObject("edition_date", LocalDateTime.class));
        builder.editor(rs.getString("editor"));
        return builder.build();
    }

    void setInfoOpportunities(PlatformVersion.PlatformVersionBuilder<?, ?> platformVersionBuilder, String listOfInfoOpportunities) {
        if (listOfInfoOpportunities != null) {
            Set<InfoOpportunityAttributeType> infoOpportunities = RowMapperUtil.stringToSet(listOfInfoOpportunities, InfoOpportunityAttributeType::valueOf);
            platformVersionBuilder.infoOpportunities(infoOpportunities);
            platformVersionBuilder.infoOpportunitiesPipeList(RowMapperUtil.toPipedString(infoOpportunities));
        }
    }
}
