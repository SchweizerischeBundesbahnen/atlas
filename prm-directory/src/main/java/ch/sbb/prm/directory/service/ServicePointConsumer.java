package ch.sbb.prm.directory.service;

import ch.sbb.atlas.kafka.model.service.point.SharedServicePointVersionModel;
import ch.sbb.prm.directory.entity.ServicePoint;
import ch.sbb.prm.directory.repository.ServicePointRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ServicePointConsumer {

  private final ServicePointRepository servicePointRepository;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "${kafka.atlas.service.point.topic}", groupId = "${kafka.atlas.service.point.groupId}")
  public void readServicePointFromKafka(SharedServicePointVersionModel sharedServicePointVersionModel) {
    try {
      servicePointRepository.save(ServicePoint.builder()
          .sloid(sharedServicePointVersionModel.getServicePointSloid())
          .servicePoint(objectMapper.writeValueAsString(sharedServicePointVersionModel))
          .build());
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }
}
