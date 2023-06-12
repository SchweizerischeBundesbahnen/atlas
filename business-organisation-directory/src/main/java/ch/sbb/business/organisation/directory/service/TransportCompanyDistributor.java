package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import ch.sbb.atlas.kafka.producer.BaseProducer;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransportCompanyDistributor extends BaseProducer<SharedTransportCompanyModel> {

  @Value("${kafka.atlas.transport.company.topic}")
  @Getter
  private String topic;

  public TransportCompanyDistributor(KafkaTemplate<String, Object> kafkaTemplate) {
    super(kafkaTemplate);
  }

  public void pushToKafka(TransportCompany transportCompany) {
    produceEvent(toModel(transportCompany), String.valueOf(transportCompany.getId()));
  }

  private SharedTransportCompanyModel toModel(TransportCompany entity) {
    return SharedTransportCompanyModel.builder()
        .id(entity.getId())
        .number(entity.getNumber())
        .abbreviation(entity.getAbbreviation())
        .description(entity.getDescription())
        .businessRegisterName(entity.getBusinessRegisterName())
        .businessRegisterNumber(entity.getBusinessRegisterNumber())
        .build();
  }
}
