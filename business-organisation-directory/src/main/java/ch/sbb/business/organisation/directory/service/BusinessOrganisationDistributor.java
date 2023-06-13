package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationUpdate;
import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationVersionModel;
import ch.sbb.atlas.kafka.model.business.organisation.UpdateAction;
import ch.sbb.atlas.kafka.producer.BaseProducer;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BusinessOrganisationDistributor extends BaseProducer<SharedBusinessOrganisationUpdate> {

  @Value("${kafka.atlas.business.organisation.topic}")
  @Getter
  @Setter
  private String topic;

  public BusinessOrganisationDistributor(KafkaTemplate<String, Object> kafkaTemplate) {
    super(kafkaTemplate);
  }

  public void saveToDistributedServices(BusinessOrganisationVersion entity) {
    pushToKafka(toModel(entity), UpdateAction.SAVE);
  }

  public void deleteOnDistributedServices(BusinessOrganisationVersion entity) {
    pushToKafka(toModel(entity), UpdateAction.DELETE);
  }

  private SharedBusinessOrganisationVersionModel toModel(BusinessOrganisationVersion entity) {
    return SharedBusinessOrganisationVersionModel.builder()
        .id(entity.getId())
        .sboid(entity.getSboid())
        .abbreviationDe(entity.getAbbreviationDe())
        .abbreviationFr(entity.getAbbreviationFr())
        .abbreviationIt(entity.getAbbreviationIt())
        .abbreviationEn(entity.getAbbreviationEn())
        .descriptionDe(entity.getDescriptionDe())
        .descriptionFr(entity.getDescriptionFr())
        .descriptionIt(entity.getDescriptionIt())
        .descriptionEn(entity.getDescriptionEn())
        .organisationNumber(entity.getOrganisationNumber())
        .status(entity.getStatus())
        .validFrom(entity.getValidFrom())
        .validTo(entity.getValidTo())
        .build();
  }

  private void pushToKafka(SharedBusinessOrganisationVersionModel model, UpdateAction action) {
    pushToKafka(SharedBusinessOrganisationUpdate.builder().model(model).action(action).build());
  }

  private void pushToKafka(SharedBusinessOrganisationUpdate modelUpdate) {
    produceEvent(modelUpdate, String.valueOf(modelUpdate.getModel().getId()));
  }
}
