package ch.sbb.atlas.transport.company.service;

import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import ch.sbb.atlas.transport.company.repository.TransportCompanySharingDataAccessor;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
public class SharedTransportCompanyConsumer {

  private final TransportCompanySharingDataAccessor transportCompanySharingDataAccessor;

  @KafkaListener(topics = "${kafka.atlas.transport.company.topic}", groupId = "${kafka.atlas.transport.company.groupId}")
  public void readTransportCompanyFromKafka(SharedTransportCompanyModel model) {
    transportCompanySharingDataAccessor.save(model);
  }

}
