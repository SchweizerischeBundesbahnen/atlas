package ch.sbb.atlas.business.organisation.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.business.organisation.repository.BusinessOrganisationVersionSharingDataAccessor;
import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationUpdate;
import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationVersionModel;
import ch.sbb.atlas.kafka.model.business.organisation.UpdateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SharedBusinessOrganisationConsumerTest {

  @Mock
  private BusinessOrganisationVersionSharingDataAccessor businessOrganisationVersionSharingDataAccessor;

  private SharedBusinessOrganisationConsumer sharedBusinessOrganisationConsumer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sharedBusinessOrganisationConsumer = new SharedBusinessOrganisationConsumer(businessOrganisationVersionSharingDataAccessor);
  }

  @Test
  void shouldSaveModelToDatabase() {
    // given
    SharedBusinessOrganisationVersionModel version = SharedBusinessOrganisationVersionModel.builder()
        .id(1L)
        .build();
    SharedBusinessOrganisationUpdate modelUpdate = SharedBusinessOrganisationUpdate.builder()
        .model(version)
        .action(UpdateAction.SAVE)
        .build();

    // when
    sharedBusinessOrganisationConsumer.readBusinessOrganisationsFromKafka(modelUpdate);

    // then
    verify(businessOrganisationVersionSharingDataAccessor).save(any());
  }

  @Test
  void shouldDeleteModelFromDatabase() {
    // given
    SharedBusinessOrganisationVersionModel version = SharedBusinessOrganisationVersionModel.builder()
        .id(1L)
        .build();
    SharedBusinessOrganisationUpdate modelUpdate = SharedBusinessOrganisationUpdate.builder()
        .model(version)
        .action(UpdateAction.DELETE)
        .build();

    // when
    sharedBusinessOrganisationConsumer.readBusinessOrganisationsFromKafka(modelUpdate);

    // then
    verify(businessOrganisationVersionSharingDataAccessor).deleteById(1L);
  }
}