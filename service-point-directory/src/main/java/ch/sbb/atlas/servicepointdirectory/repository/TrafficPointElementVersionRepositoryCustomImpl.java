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
                                         MapSqlParameterSource mapSqlParameterSource) {
        String query = """
                SELECT DISTINCT
                    trp.*,
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
        if (!trafficPointElementRequestParams.getServicePointNumberShort().isEmpty()) {
            query += " AND spv.number_short IN (:shorts)";
            mapSqlParameterSource.addValue("shorts", trafficPointElementRequestParams.getServicePointNumberShort());
        }
        if (!trafficPointElementRequestParams.getUicCountryCodes().isEmpty()) {
            query += " AND spv.country IN (:countries)";
            mapSqlParameterSource.addValue("countries", trafficPointElementRequestParams.getUicCountryCodes()
                    .stream()
                    .map(uicCountryCode -> Country.from(Integer.valueOf(uicCountryCode)).toString())
                    .toList());
        }

        return query;
    }
}
