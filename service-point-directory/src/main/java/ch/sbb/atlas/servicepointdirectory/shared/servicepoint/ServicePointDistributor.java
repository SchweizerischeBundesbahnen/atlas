package ch.sbb.atlas.servicepointdirectory.shared.servicepoint;

import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.atlas.kafka.producer.BaseProducer;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.servicepointdirectory.module.trafficpoint.entity.TrafficPointElementVersion;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServicePointDistributor extends BaseProducer<SharedServicePointVersionModel> {

  @Value("${kafka.atlas.service.point.topic}")
  @Getter
  @Setter
  private String topic;

  private final SharedServicePointRepository sharedServicePointRepository;

  public ServicePointDistributor(KafkaTemplate<String, Object> kafkaTemplate,
      SharedServicePointRepository sharedServicePointRepository) {
    super(kafkaTemplate);
    this.sharedServicePointRepository = sharedServicePointRepository;
  }

  public void publishTrafficPointElement(TrafficPointElementVersion trafficPointElementVersion) {
    publishServicePointsWithNumbers(Set.of(trafficPointElementVersion.getServicePointNumber()));
  }

  public void publishTrafficPointElements(List<TrafficPointElementVersion> updatedTrafficPoint) {
    Set<ServicePointNumber> servicePointNumbers = updatedTrafficPoint.stream()
        .map(TrafficPointElementVersion::getServicePointNumber).collect(Collectors.toSet());
    publishServicePointsWithNumbers(servicePointNumbers);
  }

  public void publishServicePointsWithNumbers(ServicePointNumber number) {
    publishServicePointsWithNumbers(Set.of(number));
  }

  public void publishServicePointsWithNumbers(Set<ServicePointNumber> numbers) {
    sharedServicePointRepository.getServicePoints(numbers).forEach(this::publish);
  }

  public void syncServicePoints() {
    Set<SharedServicePointVersionModel> servicePoints = sharedServicePointRepository.getAllServicePoints();
    servicePoints.forEach(this::publish);
  }

  private void publish(SharedServicePointVersionModel sharedServicePointVersionModel) {
    produceEvent(sharedServicePointVersionModel, sharedServicePointVersionModel.getServicePointSloid());
  }

  @Component
  @RequiredArgsConstructor
  public static class SharedServicePointRepository {

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
            boolean stopPoint = resultSet.getBoolean("stop_point");

            if (servicePointSearchResults.containsKey(servicePointSloid)) {
              SharedServicePointVersionModel sharedServicePointVersionModel = servicePointSearchResults.get(servicePointSloid);
              sharedServicePointVersionModel.getSboids().add(businessOrganisation);
              if (StringUtils.isNotBlank(trafficPointElementSloid)) {
                sharedServicePointVersionModel.getTrafficPointSloids().add(trafficPointElementSloid);
              }
              if (stopPoint) {
                sharedServicePointVersionModel.setStopPoint(true);
              }
            } else {
              SharedServicePointVersionModel servicePoint = SharedServicePointVersionModel.builder()
                  .servicePointSloid(servicePointSloid)
                  .sboids(new HashSet<>(Set.of(businessOrganisation)))
                  .trafficPointSloids(new HashSet<>())
                  .stopPoint(stopPoint)
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
      paramSource.addValue("mean_of_transport_unknown", MeanOfTransport.UNKNOWN.name());
      return paramSource;
    }

    private String getSqlQuery() {
      return """
          select sp.sloid as service_point_sloid,
                 sp.business_organisation,
                 tp.sloid as traffic_point_element_sloid,
                 CASE WHEN spvmot.means_of_transport != :mean_of_transport_unknown THEN 'true' ELSE 'false' END as stop_point
          from service_point_version sp
               left join service_point_version_means_of_transport spvmot
                  on sp.id = spvmot.service_point_version_id
               left join traffic_point_element_version tp
                  on sp.number=tp.service_point_number
          where sp.sloid is not null
          and sp.country in (:countries)
          """;
    }

  }
}
