package ch.sbb.atlas.business.organisation.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.business.organisation.repository.SharedBusinessOrganisationVersionRepository;
import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationUpdate;
import ch.sbb.atlas.kafka.model.business.organisation.SharedBusinessOrganisationVersionModel;
import ch.sbb.atlas.kafka.model.business.organisation.UpdateAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SharedBusinessOrganisationConsumerTest {

  @Mock
  private SharedBusinessOrganisationVersionRepository sharedBusinessOrganisationVersionRepository;

  private SharedBusinessOrganisationConsumer sharedBusinessOrganisationConsumer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sharedBusinessOrganisationConsumer = new SharedBusinessOrganisationConsumer(sharedBusinessOrganisationVersionRepository);
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
    verify(sharedBusinessOrganisationVersionRepository).save(any());
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
    verify(sharedBusinessOrganisationVersionRepository).deleteById(1L);
  }
}