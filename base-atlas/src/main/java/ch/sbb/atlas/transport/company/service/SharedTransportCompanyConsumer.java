package ch.sbb.atlas.transport.company.service;

import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import ch.sbb.atlas.transport.company.entity.SharedTransportCompany;
import ch.sbb.atlas.transport.company.repository.SharedTransportCompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional
public class SharedTransportCompanyConsumer {

  private final SharedTransportCompanyRepository sharedTransportCompanyRepository;

  @KafkaListener(topics = "${kafka.atlas.transport.company.topic}", groupId = "${kafka.atlas.transport.company.groupId}")
  public void readTransportCompanyFromKafka(SharedTransportCompanyModel model) {
    sharedTransportCompanyRepository.save(toEntity(model));
  }

  private SharedTransportCompany toEntity(SharedTransportCompanyModel model) {
    return SharedTransportCompany.builder()
        .id(model.getId())
        .number(model.getNumber())
        .abbreviation(model.getAbbreviation())
        .description(model.getDescription())
        .businessRegisterName(model.getBusinessRegisterName())
        .businessRegisterNumber(model.getBusinessRegisterNumber())
        .build();
  }

}
