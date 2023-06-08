package ch.sbb.atlas.business.organisation.service;

import ch.sbb.atlas.business.organisation.entity.SharedBusinessOrganisationVersion;
import ch.sbb.atlas.business.organisation.repository.SharedBusinessOrganisationVersionRepository;
import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationUpdate;
import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationVersionModel;
import ch.sbb.atlas.kafka.model.business.organisation.UpdateAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional
public class SharedBusinessOrganisationConsumer {

  private final SharedBusinessOrganisationVersionRepository sharedBusinessOrganisationVersionRepository;

  @KafkaListener(topics = "${kafka.atlas.business.organisation.topic}", groupId = "${kafka.atlas.business.organisation.groupId}")
  public void readBusinessOrganisationsFromKafka(SharedBusinessOrganisationUpdate modelUpdate) {
    if (modelUpdate.getAction() == UpdateAction.SAVE) {
      sharedBusinessOrganisationVersionRepository.save(toEntity(modelUpdate.getModel()));
    }
    if (modelUpdate.getAction() == UpdateAction.DELETE) {
      sharedBusinessOrganisationVersionRepository.deleteById(modelUpdate.getModel().getId());
    }
  }

  private SharedBusinessOrganisationVersion toEntity(
      SharedBusinessOrganisationVersionModel sharedBusinessOrganisationVersionModel) {
    return SharedBusinessOrganisationVersion.builder()
        .id(sharedBusinessOrganisationVersionModel.getId())
        .sboid(sharedBusinessOrganisationVersionModel.getSboid())
        .abbreviationDe(sharedBusinessOrganisationVersionModel.getAbbreviationDe())
        .abbreviationFr(sharedBusinessOrganisationVersionModel.getAbbreviationFr())
        .abbreviationIt(sharedBusinessOrganisationVersionModel.getAbbreviationIt())
        .abbreviationEn(sharedBusinessOrganisationVersionModel.getAbbreviationEn())
        .descriptionDe(sharedBusinessOrganisationVersionModel.getDescriptionDe())
        .descriptionFr(sharedBusinessOrganisationVersionModel.getDescriptionFr())
        .descriptionIt(sharedBusinessOrganisationVersionModel.getDescriptionIt())
        .descriptionEn(sharedBusinessOrganisationVersionModel.getDescriptionEn())
        .organisationNumber(sharedBusinessOrganisationVersionModel.getOrganisationNumber())
        .status(sharedBusinessOrganisationVersionModel.getStatus())
        .validFrom(sharedBusinessOrganisationVersionModel.getValidFrom())
        .validTo(sharedBusinessOrganisationVersionModel.getValidTo())
        .build();
  }

}
