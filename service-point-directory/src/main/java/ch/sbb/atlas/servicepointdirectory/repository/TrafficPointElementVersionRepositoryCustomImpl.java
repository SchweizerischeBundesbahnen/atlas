package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.TrafficPointElementType;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.entity.geolocation.TrafficPointElementGeolocation;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementRequestParams;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class TrafficPointElementVersionRepositoryCustomImpl implements TrafficPointElementVersionRepositoryCustom {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Page<TrafficPointElementVersion> blaBloBlu2(TrafficPointElementRequestParams trafficPointElementRequestParams, Pageable pageable) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        String query = getQueryString(trafficPointElementRequestParams, mapSqlParameterSource, pageable);

        Integer count = getCount(query, mapSqlParameterSource);

        query += " ORDER BY sloid";
        if (pageable != null && pageable.isPaged()) {
            query += " LIMIT :pageSize OFFSET :offset";
            mapSqlParameterSource.addValue("pageSize", pageable.getPageSize());
            mapSqlParameterSource.addValue("offset", pageable.getOffset());
        }

        List<TrafficPointElementVersion> elements = jdbcTemplate.query(query, mapSqlParameterSource, (rs, rowNum) -> {
            return TrafficPointElementVersion.builder()
                    .id(rs.getLong("id"))
                    .designation(rs.getString("designation"))
                    .designationOperational(rs.getString("designation_operational"))
                    .length(rs.getDouble("length"))
                    .boardingAreaHeight(rs.getDouble("boarding_area_height"))
                    .compassDirection(rs.getDouble("compass_direction"))
                    .trafficPointElementType(TrafficPointElementType.fromValue(rs.getInt("traffic_point_element_type")))
                    .servicePointNumber(ServicePointNumber.of(rs.getInt("service_point_number")))
                    .sloid(rs.getString("sloid"))
                    .parentSloid(rs.getString("parent_sloid"))
                    .trafficPointElementGeolocation(TrafficPointElementGeolocation.builder()
                            .id(rs.getLong("traffic_point_geolocation_id"))
                            .height(rs.getDouble("height"))
                            .east(rs.getDouble("east"))
                            .north(rs.getDouble("north"))
                            .spatialReference(SpatialReference.valueOf(rs.getString("spatial_reference")))
                            .creationDate(rs.getTimestamp("creation_date").toLocalDateTime())
                            .editionDate(rs.getTimestamp("edition_date").toLocalDateTime())
                            .creator(rs.getString("creator"))
                            .editor(rs.getString("editor"))
                            .version(rs.getInt("version"))
                            .build())
                    .validFrom(rs.getDate("valid_from").toLocalDate())
                    .validTo(rs.getDate("valid_to").toLocalDate())
                    .creationDate(rs.getTimestamp("creation_date").toLocalDateTime())
                    .editionDate(rs.getTimestamp("edition_date").toLocalDateTime())
                    .creator(rs.getString("creator"))
                    .editor(rs.getString("editor"))
                    .version(rs.getInt("version"))
                    .build();
        });


        return new PageImpl<>(elements, pageable, count);

    }

    private Integer getCount(String query, MapSqlParameterSource mapSqlParameterSource) {
        String countQuery = "select count (*) from (" + query + ") as foo";
        return jdbcTemplate.queryForObject(countQuery, mapSqlParameterSource, Integer.class);
    }

    private static String getQueryString(TrafficPointElementRequestParams trafficPointElementRequestParams,
                                         MapSqlParameterSource mapSqlParameterSource,
                                         Pageable pageable) {
        String query = """
                SELECT DISTINCT
                    trp.id,
                    trp.sloid,
                    trp.parent_sloid,
                    trp.designation,
                    trp.designation_operational,
                    trp.traffic_point_element_type,
                    trp.length,
                    trp.boarding_area_height,
                    trp.compass_direction,
                    trp.service_point_number,
                    trp.valid_from,
                    trp.valid_to,
                    trp.traffic_point_geolocation_id,
                    trp.creation_date,
                    trp.creator,
                    trp.edition_date,
                    trp.editor,
                    trp.version,
                    tpevg.*
                FROM
                    traffic_point_element_version trp
                LEFT JOIN
                    service_point_version spv ON trp.service_point_number = spv.number
                LEFT JOIN traffic_point_element_version_geolocation tpevg ON tpevg.id = trp.traffic_point_geolocation_id
                WHERE 1=1
                """;

        if (!trafficPointElementRequestParams.getBusinessOrganisations().isEmpty()) {
            query += " AND spv.business_organisation IN (:sboids)";
            mapSqlParameterSource.addValue("sboids", trafficPointElementRequestParams.getBusinessOrganisations());
        }
//        if (!trafficPointElementRequestParams.getServicePointNumberShort().isEmpty()) {
//            query += " and spv.number_short in (:shorts)";
//        }

        return query;
    }
}
