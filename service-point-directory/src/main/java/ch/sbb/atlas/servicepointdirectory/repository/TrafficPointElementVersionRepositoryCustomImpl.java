package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.imports.servicepoint.enumeration.SpatialReference;
import ch.sbb.atlas.servicepoint.Country;
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
    public Page<TrafficPointElementVersion> findByServicePointParameters(TrafficPointElementRequestParams trafficPointElementRequestParams, Pageable pageable) {
    public Page<TrafficPointElementVersion> findByServicePointParameters(TrafficPointElementRequestParams trafficPointElementRequestParams, Pageable pageable) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        String query = getQueryString(trafficPointElementRequestParams, mapSqlParameterSource);

        Integer count = getCount(query, mapSqlParameterSource);

        query += " ORDER BY sloid";
        if (pageable != null && pageable.isPaged()) {
            query += " LIMIT :pageSize OFFSET :offset";
            mapSqlParameterSource.addValue("pageSize", pageable.getPageSize());
            mapSqlParameterSource.addValue("offset", pageable.getOffset());
        }

        List<TrafficPointElementVersion> elements = jdbcTemplate.query(query, mapSqlParameterSource, (rs, rowNum) -> {
            return TrafficPointElementVersion.builder()
                    .id(rs.getLong("id1"))
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
                            .creationDate(rs.getTimestamp("creation_date2").toLocalDateTime())
                            .editionDate(rs.getTimestamp("edition_date2").toLocalDateTime())
                            .creator(rs.getString("creator2"))
                            .editor(rs.getString("editor2"))
                            .version(rs.getInt("version2"))
                            .build())
                    .validFrom(rs.getDate("valid_from").toLocalDate())
                    .validTo(rs.getDate("valid_to").toLocalDate())
                    .creationDate(rs.getTimestamp("creation_date1").toLocalDateTime())
                    .editionDate(rs.getTimestamp("edition_date1").toLocalDateTime())
                    .creator(rs.getString("creator1"))
                    .editor(rs.getString("editor1"))
                    .version(rs.getInt("version1"))
                    .build();
        });


        return new PageImpl<>(elements, pageable, count);

    }

    private Integer getCount(String query, MapSqlParameterSource mapSqlParameterSource) {
        String countQuery = "select count (*) from (" + query + ") as foo";
        return jdbcTemplate.queryForObject(countQuery, mapSqlParameterSource, Integer.class);
    }

    private static String getQueryString(TrafficPointElementRequestParams trafficPointElementRequestParams,
                                         MapSqlParameterSource mapSqlParameterSource) {
        String query = """
                SELECT DISTINCT
                    trp.id as id1,
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
                    trp.creation_date as creation_date1,
                    trp.creator as creator1,
                    trp.edition_date as edition_date1,
                    trp.editor as editor1,
                    trp.version as version1,
                    tpevg.id as id2,
                    tpevg.spatial_reference,
                    tpevg.height,
                    tpevg.east,
                    tpevg.north,
                    tpevg.creation_date as creation_date2,
                    tpevg.creator as creator2,
                    tpevg.edition_date as edition_date2,
                    tpevg.editor as editor2,
                    tpevg.version as version2
                FROM
                    traffic_point_element_version trp
                LEFT JOIN
                    service_point_version spv ON trp.service_point_number = spv.number
                LEFT JOIN traffic_point_element_version_geolocation tpevg ON tpevg.id = trp.traffic_point_geolocation_id
                WHERE 1=1
                """;

        if (!trafficPointElementRequestParams.getSboids().isEmpty()) {
            query += " AND spv.business_organisation IN (:sboids)";
            mapSqlParameterSource.addValue("sboids", trafficPointElementRequestParams.getSboids());
        }
        if (!trafficPointElementRequestParams.getServicePointNumbersShort().isEmpty()) {
            query += " AND spv.number_short IN (:shorts)";
            mapSqlParameterSource.addValue("shorts", trafficPointElementRequestParams.getServicePointNumbersShort());
        }
        if (!trafficPointElementRequestParams.getUicCountryCodes().isEmpty()) {
            query += " AND spv.country IN (:countries)";
            mapSqlParameterSource.addValue("countries", trafficPointElementRequestParams.getUicCountryCodes()
                    .stream()
                    .map(uicCountryCode -> Country.from(Integer.valueOf(uicCountryCode)).toString())
                    .toList());
        }
        if (!trafficPointElementRequestParams.getSloids().isEmpty()) {
            query += " AND trp.sloid IN (:sloids)";
            mapSqlParameterSource.addValue("sloids", trafficPointElementRequestParams.getSloids());
        }
        if (!trafficPointElementRequestParams.getParentsloids().isEmpty()) {
            query += " AND trp.parent_sloid IN (:psloids)";
            mapSqlParameterSource.addValue("psloids", trafficPointElementRequestParams.getParentsloids());
        }
        if (trafficPointElementRequestParams.getFromDate() != null) {
            query += " AND trp.valid_from = :validfrom";
            mapSqlParameterSource.addValue("validfrom", trafficPointElementRequestParams.getFromDate());
        }
        if (trafficPointElementRequestParams.getToDate() != null) {
            query += " AND trp.valid_to = :validto";
            mapSqlParameterSource.addValue("validto", trafficPointElementRequestParams.getToDate());
        }
        if (trafficPointElementRequestParams.getCreatedAfter() != null) {
            query += " AND trp.creation_date > :createdafter";
            mapSqlParameterSource.addValue("createdafter", trafficPointElementRequestParams.getCreatedAfter());
        }
        if (trafficPointElementRequestParams.getModifiedAfter() != null) {
            query += " AND trp.edition_date > :modifiedafter";
            mapSqlParameterSource.addValue("modifiedafter", trafficPointElementRequestParams.getModifiedAfter());
        }
        return new PageImpl<>(elements, pageable, count);

    }

    private Integer getCount(String query, MapSqlParameterSource mapSqlParameterSource) {
        String countQuery = "select count (*) from (" + query + ") as foo";
        return jdbcTemplate.queryForObject(countQuery, mapSqlParameterSource, Integer.class);
    }

}
