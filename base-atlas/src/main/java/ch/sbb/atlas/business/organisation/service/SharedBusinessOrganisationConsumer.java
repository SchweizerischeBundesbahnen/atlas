package ch.sbb.atlas.business.organisation.service;

import ch.sbb.atlas.business.organisation.repository.BusinessOrganisationVersionSharingDataAccessor;
import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationUpdate;
import ch.sbb.atlas.kafka.model.business.organisation.UpdateAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Transactional
public class SharedBusinessOrganisationConsumer {

  private final BusinessOrganisationVersionSharingDataAccessor businessOrganisationVersionSharingDataAccessor;

  @KafkaListener(topics = "${kafka.atlas.business.organisation.topic}", groupId = "${kafka.atlas.business.organisation.groupId}")
  public void readBusinessOrganisationsFromKafka(SharedBusinessOrganisationUpdate modelUpdate) {
    if (modelUpdate.getAction() == UpdateAction.SAVE) {
      businessOrganisationVersionSharingDataAccessor.save(modelUpdate.getModel());
    }
    if (modelUpdate.getAction() == UpdateAction.DELETE) {
      businessOrganisationVersionSharingDataAccessor.deleteById(modelUpdate.getModel().getId());
    }
  }

}
