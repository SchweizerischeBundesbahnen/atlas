package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.atlas.kafka.producer.BaseProducer;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.model.search.TrafficPointElementSearchRestrictions;
import ch.sbb.atlas.servicepointdirectory.service.servicepoint.ServicePointService;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementRequestParams;
import ch.sbb.atlas.servicepointdirectory.service.trafficpoint.TrafficPointElementService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ServicePointDistributor extends BaseProducer<SharedServicePointVersionModel> {

  @Value("${kafka.atlas.service.point.topic}")
  @Getter
  @Setter
  private String topic;

  private final ServicePointService servicePointService;
  private final TrafficPointElementService trafficPointElementService;

  public ServicePointDistributor(KafkaTemplate<String, Object> kafkaTemplate,
      ServicePointService servicePointService, TrafficPointElementService trafficPointElementService) {
    super(kafkaTemplate);
    this.servicePointService = servicePointService;
    this.trafficPointElementService = trafficPointElementService;
  }

  public void publishServicePointVersion(ServicePointVersion servicePointVersion) {
    publishServicePointVersions(List.of(servicePointVersion));
  }

  public void publishServicePointVersions(List<ServicePointVersion> servicePointVersions) {
    ServicePointNumber servicePointNumber = servicePointVersions.iterator().next().getNumber();

    List<TrafficPointElementVersion> relatedTrafficPoints =
        trafficPointElementService.findAll(TrafficPointElementSearchRestrictions.builder()
            .pageable(Pageable.unpaged())
            .trafficPointElementRequestParams(TrafficPointElementRequestParams.builder()
                .servicePointNumbers(List.of(servicePointNumber.asString()))
                .build()).build()).getContent();

    publish(servicePointVersions, relatedTrafficPoints);
  }

  public void publishTrafficPointElement(TrafficPointElementVersion trafficPointElementVersion) {
    publishServicePointsWithNumbers(Set.of(trafficPointElementVersion.getServicePointNumber()));
  }

  public void publishTrafficPointElements(List<TrafficPointElementVersion> updatedTrafficPoint) {
    Set<ServicePointNumber> servicePointNumbers = updatedTrafficPoint.stream()
        .map(TrafficPointElementVersion::getServicePointNumber).collect(Collectors.toSet());
    publishServicePointsWithNumbers(servicePointNumbers);
  }

  public void publishServicePointsWithNumbers(Set<ServicePointNumber> numbers) {
    numbers.forEach(servicePointNumber -> {
      List<ServicePointVersion> servicePoint = servicePointService.findAllByNumberOrderByValidFrom(servicePointNumber);
      publishServicePointVersions(servicePoint);
    });
  }

  private void publish(List<ServicePointVersion> servicePoint, List<TrafficPointElementVersion> trafficPoint) {
    String servicePointSloid = servicePoint.iterator().next().getSloid();
    Set<String> sboids = servicePoint.stream().map(ServicePointVersion::getBusinessOrganisation).collect(Collectors.toSet());
    Set<String> trafficPointSloids = trafficPoint.stream().map(TrafficPointElementVersion::getSloid).collect(Collectors.toSet());

    SharedServicePointVersionModel sharedServicePointVersionModel = SharedServicePointVersionModel.builder()
        .servicePointSloid(servicePointSloid)
        .sboids(sboids)
        .trafficPointSloids(trafficPointSloids)
        .build();
    produceEvent(sharedServicePointVersionModel, sharedServicePointVersionModel.getServicePointSloid());
  }

}
