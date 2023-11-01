package ch.sbb.atlas.servicepointdirectory.repository;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.SharedServicePointVersionModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class SharedServicePointRepository {

  private final NamedParameterJdbcTemplate jdbcTemplate;

  public Set<SharedServicePointVersionModel> getAllServicePoints() {
    return getServicePoints(getSqlQuery(), getBaseParameters());
  }

  public Set<SharedServicePointVersionModel> getServicePoints(Set<ServicePointNumber> servicePointNumbers) {
    if (servicePointNumbers.isEmpty()) {
      return getAllServicePoints();
    }

    String baseQuery = getSqlQuery();
    baseQuery += "and sp.number in (:numbers)";

    MapSqlParameterSource parameters = getBaseParameters();
    parameters.addValue("numbers", servicePointNumbers.stream().map(ServicePointNumber::getValue).toList());

    return getServicePoints(baseQuery, parameters);
  }

  private Set<SharedServicePointVersionModel> getServicePoints(String query, MapSqlParameterSource paramSource) {
    Map<String, SharedServicePointVersionModel> servicePointSearchResults = new HashMap<>();

    jdbcTemplate.query(query, paramSource,
        (resultSet) -> {
          String servicePointSloid = resultSet.getString("service_point_sloid");
          String businessOrganisation = resultSet.getString("business_organisation");
          String trafficPointElementSloid = resultSet.getString("traffic_point_element_sloid");

          if (servicePointSearchResults.containsKey(servicePointSloid)) {
            SharedServicePointVersionModel sharedServicePointVersionModel = servicePointSearchResults.get(servicePointSloid);
            sharedServicePointVersionModel.getSboids().add(businessOrganisation);
            if (StringUtils.isNotBlank(trafficPointElementSloid)) {
              sharedServicePointVersionModel.getTrafficPointSloids().add(trafficPointElementSloid);
            }
          } else {
            SharedServicePointVersionModel servicePoint = SharedServicePointVersionModel.builder()
                .servicePointSloid(servicePointSloid)
                .sboids(new HashSet<>(Set.of(businessOrganisation)))
                .trafficPointSloids(new HashSet<>())
                .build();
            if (StringUtils.isNotBlank(trafficPointElementSloid)) {
              servicePoint.getTrafficPointSloids().add(trafficPointElementSloid);
            }
            servicePointSearchResults.put(servicePointSloid, servicePoint);
          }
        }
    );

    return new HashSet<>(servicePointSearchResults.values());
  }

  private static MapSqlParameterSource getBaseParameters() {
    MapSqlParameterSource paramSource = new MapSqlParameterSource();
    paramSource.addValue("countries", Stream.of(Country.SWITZERLAND).map(Enum::name).toList());
    return paramSource;
  }

  private String getSqlQuery() {
    return """
        select sp.sloid as service_point_sloid,
               sp.business_organisation,
               tp.sloid as traffic_point_element_sloid
        from service_point_version sp
             left join traffic_point_element_version tp
             on sp.number=tp.service_point_number
        where sp.sloid is not null
        and sp.country in (:countries)
        """;
  }

}
