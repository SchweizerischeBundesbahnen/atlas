package ch.sbb.atlas.servicepointdirectory.service;

import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.atlas.kafka.producer.BaseProducer;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.TrafficPointElementVersion;
import ch.sbb.atlas.servicepointdirectory.repository.SharedServicePointRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
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

}
