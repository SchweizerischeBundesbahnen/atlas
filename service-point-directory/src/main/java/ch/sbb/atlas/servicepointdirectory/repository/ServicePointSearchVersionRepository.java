package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.api.ServicePointSearchResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ServicePointSearchVersionRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<ServicePointSearchResult> searchServicePoints(String value) {
        String sanitizeValue = value.trim();
        if(NumberUtils.isParsable(value.replaceAll("\\s", ""))){
            sanitizeValue = value.replaceAll("\\s", "");
        }
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("contains_value", "%" + sanitizeValue + "%");
        mapSqlParameterSource.addValue("perfect_match", sanitizeValue);
        mapSqlParameterSource.addValue("ends_with_with_space", sanitizeValue + " %");
        mapSqlParameterSource.addValue("starts_with", sanitizeValue + "%");
        mapSqlParameterSource.addValue("ends_with", "%" + sanitizeValue);

        String query = getSqlQuery(value);

        List<ServicePointSearchResult> servicePointSearchResults = jdbcTemplate.query(
                query,
                mapSqlParameterSource,
                (rs, rowNum) -> {
                    rs.setFetchSize(1000);
                    return new ServicePointSearchResult(
                            ServicePointNumber.ofNumberWithoutCheckDigit(rs.getInt("number")),
                            rs.getString("designation_official"));
                }
        );

        return new ArrayList<>(new LinkedHashSet<>(servicePointSearchResults));
    }

    private String getSqlQuery(String value) {
        return """
                select number, designation_official
                from service_point_version
                where upper(cast(number as text)) like upper( :contains_value)
                    or replace(upper(designation_official), ',','') like replace(upper(:contains_value), ',','')
                    or replace(upper(designation_long), ',','') like replace(upper(:contains_value), ',','')
                    or replace(upper(abbreviation), ',','') like replace(upper(:contains_value), ',','')
                order by
                    (case
                        when cast(number as text) like '85%' then 0
                        else 1 end),
                    $dynamicCases
                    (case
                        when designation_long = :perfect_match then 0
                        when replace(upper(designation_long), ',','') like replace(upper(:perfect_match), ',','') then 1
                        when designation_long like :ends_with_with_space then 2
                        when replace(upper(designation_long), ',','') like replace(upper(:ends_with_with_space), ',','') then 3
                        when designation_long like :starts_with then 4
                        when designation_long like :ends_with then 5
                        when designation_long like :contains_value then 6
                        else 8 end),
                    (case
                        when abbreviation = :perfect_match then 0
                        when replace(upper(abbreviation), ',','') like replace(upper(:perfect_match), ',','') then 1
                        when abbreviation like :ends_with_with_space then 2
                        when replace(upper(abbreviation), ',','') like replace(upper(:ends_with_with_space), ',','') then 3
                        when abbreviation like :starts_with then 4
                        when abbreviation like :ends_with then 5
                        when abbreviation like :contains_value then 6
                        else 7 end)
                        limit 1000
                """.replace("$dynamicCases", getDynamicCases(value));
    }

    private String getDynamicCases(String value) {
        if (NumberUtils.isParsable(value)) {
            return getNumberCase() + ",\n" + getDesignationOfficialCase() + ",\n";
        }
        return getDesignationOfficialCase() + ",\n" + getNumberCase() + ",\n";
    }

    private String getDesignationOfficialCase() {
        return """
                (case
                    when designation_official = :perfect_match then 0
                    when replace(upper(designation_official), ',','') like replace(upper(:perfect_match), ',','') then 1
                    when designation_official like :ends_with_with_space then 2
                    when replace(upper(designation_official), ',','') like replace(upper(:ends_with_with_space), ',','') then 3
                    when designation_official like :starts_with then 4
                    when designation_official like :ends_with then 5
                    when designation_official like :contains_value then 6
                    else 7 end
                )
                """;
    }

    private String getNumberCase() {
        return """
                (case
                    when cast(number as text) = :perfect_match then 0
                    when cast(number as text) like :ends_with_with_space then 1
                    when cast(number as text) like :starts_with then 2
                    when cast(number as text) like :ends_with then 3
                    when cast(number as text) like :contains_value then 4
                    else 5 end
                )
                """;
    }

}
