package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointSearchResult;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
  public static final String JOIN_MEANS_OF_TRANSPORT = "join service_point_version_means_of_transport as spvmt "
      + "on spv.id = spvmt.service_point_version_id";

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public List<ServicePointSearchResult> searchServicePoints(String value) {
    return searchServicePoints(value, false, false);
  }

  public List<ServicePointSearchResult> searchServicePointsWithRouteNetworkTrue(String value) {
    return searchServicePoints(value, true, false);
  }

  public List<ServicePointSearchResult> searchSwissOnlyStopPointServicePoints(String value) {
    return searchServicePoints(value, false, true);
  }

  private List<ServicePointSearchResult> searchServicePoints(String value, boolean isOperationPointRouteNetworkTrue,
      boolean isSwissOnlyStopPointServicePoint) {

    validateInput(value);
    String sanitizeValue = sanitizeValue(value);
    MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
    mapSqlParameterSource.addValue("perfect_match", sanitizeValue);
    mapSqlParameterSource.addValue("starts_with", sanitizeValue + "%");
    mapSqlParameterSource.addValue("starts_with_space", sanitizeValue + " %");
    mapSqlParameterSource.addValue("ends_with", "%" + sanitizeValue);
    mapSqlParameterSource.addValue("ends_with_space", "% " + sanitizeValue);
    mapSqlParameterSource.addValue("contains_value", "%" + sanitizeValue + "%");
    mapSqlParameterSource.addValue("swiss_country_code", Country.SWITZERLAND.getUicCode().toString() + "%");

    if (isSwissOnlyStopPointServicePoint) {
      mapSqlParameterSource.addValue("countries", SWISS_ONLY_COUNTRIES);
      mapSqlParameterSource.addValue("not_unknown_means_of_transport", MeanOfTransport.UNKNOWN.name());
    }

    String query = getSqlQuery(value, isOperationPointRouteNetworkTrue, isSwissOnlyStopPointServicePoint);

    List<ServicePointSearchResult> servicePointSearchResults = jdbcTemplate.query(
        query,
        mapSqlParameterSource,
        (rs, rowNum) -> {
          rs.setFetchSize(FETCH_SIZE);
          return new ServicePointSearchResult(
              rs.getInt("number"),
              rs.getString("designation_official"),
              rs.getString("sloid")
          );
        }
    );

    return new ArrayList<>(new LinkedHashSet<>(servicePointSearchResults));
  }

  private String getSqlQuery(String value, boolean isOperationPointRouteNetworkTrue, boolean isSwissOnlyStopPointServicePoint) {
    return """
        select number, designation_official, sloid
        from service_point_version as spv
        $MEANS_OF_TRANSPORT_JOIN_CLAUSE
        where (upper(cast(number as text)) like upper( :contains_value)
            or replace(upper(designation_official), ',','') like replace(upper(:contains_value), ',','')
            or replace(upper(designation_long), ',','') like replace(upper(:contains_value), ',',''))
        $MEANS_OF_TRANSPORT_NOT_UNKNOWN_CLAUSE
        $OPERATING_POINT_ROUTE_NETWORK_CLAUSE
        $SWISS_ONLY_COUNTRY_CLAUSE
        order by
            (case
                when cast(number as text) like :swiss_country_code then 0
                else 1 end),
            $DYNAMIC_CASES_CLAUSE
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
        """
        .replace("$DYNAMIC_CASES_CLAUSE", getDynamicCases(value))
        .replace("$SWISS_ONLY_COUNTRY_CLAUSE", addIsSwissOnlyClause(isSwissOnlyStopPointServicePoint))
        .replace("$MEANS_OF_TRANSPORT_NOT_UNKNOWN_CLAUSE", addIsMeansOfTransportNotUnknownClause(isSwissOnlyStopPointServicePoint))
        .replace("$MEANS_OF_TRANSPORT_JOIN_CLAUSE", addMeansOfTransportJoinClause(isSwissOnlyStopPointServicePoint))
        .replace("$OPERATING_POINT_ROUTE_NETWORK_CLAUSE",
            addIsOperationPointRouteNetworkTrueClause(isOperationPointRouteNetworkTrue));

  }

  private String addIsSwissOnlyClause(boolean isSwissOnlyStopPointServicePoint) {
    return isSwissOnlyStopPointServicePoint ? "and country in (:countries) " : StringUtils.EMPTY;
  }

  private String addIsMeansOfTransportNotUnknownClause(boolean isSwissOnlyStopPointServicePoint) {
    return isSwissOnlyStopPointServicePoint ? "and spvmt.means_of_transport != :not_unknown_means_of_transport "
        : StringUtils.EMPTY;
  }

  private String addMeansOfTransportJoinClause(boolean isSwissOnlyStopPointServicePoint) {
    return isSwissOnlyStopPointServicePoint ? JOIN_MEANS_OF_TRANSPORT : StringUtils.EMPTY;
  }

  private String addIsOperationPointRouteNetworkTrueClause(boolean isOperationPointRouteNetworkTrue) {
    return isOperationPointRouteNetworkTrue ? "and operating_point_route_network = true " : StringUtils.EMPTY;
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

}
