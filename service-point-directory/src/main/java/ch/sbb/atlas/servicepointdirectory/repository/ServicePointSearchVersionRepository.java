package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchResult;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServicePointSearchVersionRepository {
    private static final int FETCH_SIZE = 10;
    private static final int MIN_DIGIT_SEARCH = 2;

    private static final List<String> SWISS_ONLY_COUNTRIES = Stream.of(Country.SWITZERLAND).map(Enum::name).toList();

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<ServicePointSearchResult> searchServicePoints(String value) {
        return searchServicePoints(value, false,false);
    }

    public List<ServicePointSearchResult> searchServicePointsWithRouteNetworkTrue(String value) {
        return searchServicePoints(value, true,false);
    }

    public List<ServicePointSearchResult> searchSwissOnlyServicePoints(String value) {
        return searchServicePoints(value, false,true);
    }

    private List<ServicePointSearchResult> searchServicePoints(String value, boolean isOperationPointRouteNetworkTrue,
        boolean isSwissOnly) {

        validateInput(value);
        String sanitizeValue = sanitizeValue(value);
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("perfect_match", sanitizeValue);
        mapSqlParameterSource.addValue("starts_with", sanitizeValue + "%");
        mapSqlParameterSource.addValue("starts_with_space", sanitizeValue + " %");
        mapSqlParameterSource.addValue("ends_with", "%" + sanitizeValue);
        mapSqlParameterSource.addValue("ends_with_space", "% " + sanitizeValue);
        mapSqlParameterSource.addValue("contains_value", "%" + sanitizeValue + "%");

        if(isSwissOnly){
            mapSqlParameterSource.addValue("countries", SWISS_ONLY_COUNTRIES);
        }

        String query = getSqlQuery(value, isOperationPointRouteNetworkTrue, isSwissOnly);

        List<ServicePointSearchResult> servicePointSearchResults = jdbcTemplate.query(
                query,
                mapSqlParameterSource,
                (rs, rowNum) -> {
                    rs.setFetchSize(FETCH_SIZE);
                    return new ServicePointSearchResult(
                            rs.getInt("number"),
                            rs.getString("designation_official"));
                }
        );

        return new ArrayList<>(new LinkedHashSet<>(servicePointSearchResults));
    }

    private static void validateInput(String value) {
        if (value.length() < MIN_DIGIT_SEARCH) {
            throw new IllegalArgumentException("You must enter at least 2 digits to start a search!");
        }
    }

    String sanitizeValue(String value) {
        String sanitizeValue = escapePercent(value);
        sanitizeValue = removeSpaceIfOnlyDigits(value, sanitizeValue);
        return sanitizeValue;
    }

    private static String removeSpaceIfOnlyDigits(String value, String sanitizeValue) {
        if (NumberUtils.isParsable(value.replaceAll("\\s", ""))) {
            sanitizeValue = value.replaceAll("\\s", "");
        }
        return sanitizeValue;
    }

    private static String escapePercent(String value) {
        return value.replaceAll("%", "\\\\%");
    }

    private String getSqlQuery(String value, boolean isOperationPointRouteNetworkTrue, boolean isSwissOnly) {
        String sqlQuery = """
                select number, designation_official
                from service_point_version
                where (upper(cast(number as text)) like upper( :contains_value)
                    or replace(upper(designation_official), ',','') like replace(upper(:contains_value), ',','')
                    or replace(upper(designation_long), ',','') like replace(upper(:contains_value), ',',''))
                """;
        if (isOperationPointRouteNetworkTrue) {
            sqlQuery += " and operating_point_route_network = true ";
        }
        if (isSwissOnly) {
            sqlQuery += " and country in (:countries) ";
        }
        sqlQuery += """
                order by
                    (case
                        when cast(number as text) like '85%' then 0
                        else 1 end),
                    $dynamicCases
                    (case
                        when designation_long = :perfect_match then 0
                        when upper(designation_long) like upper(:perfect_match) then 1
                        when designation_long like :starts_with then 2
                        when upper(designation_long) like upper(:starts_with) then 3
                        when designation_long like :starts_with_space then 4
                        when upper(designation_long) like upper(:starts_with_space) then 5
                        when designation_long like :ends_with then 6
                        when upper(designation_long) like upper(:ends_with) then 7
                        when designation_long like :ends_with_space then 8
                        when upper(designation_long) like upper(:ends_with_space) then 9
                        when designation_long like :contains_value then 10
                        else 11 end),
                    designation_long
                """;
        return sqlQuery.replace("$dynamicCases", getDynamicCases(value));
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
                    when upper(designation_official) like upper(:perfect_match) then 1
                    when designation_official like :starts_with then 2
                    when upper(designation_official) like upper(:starts_with) then 3
                    when designation_official like :starts_with_space then 4
                    when upper(designation_official) like upper(:starts_with_space) then 5
                    when designation_official like :ends_with then 6
                    when upper(designation_official) like upper(:ends_with) then 7
                    when designation_official like :ends_with_space then 8
                    when upper(designation_official) like upper(:ends_with_space) then 9
                    when designation_official like :contains_value then 10
                    else 11 end
                ),
                designation_official
                """;
    }

    private String getNumberCase() {
        return """
                (case
                    when cast(number as text) = :perfect_match then 0
                    when cast(number as text) like :ends_with_space then 1
                    when cast(number as text) like :starts_with then 2
                    when cast(number as text) like :ends_with then 3
                    when cast(number as text) like :contains_value then 4
                    else 5 end
                ),
                number
                """;
    }

}
