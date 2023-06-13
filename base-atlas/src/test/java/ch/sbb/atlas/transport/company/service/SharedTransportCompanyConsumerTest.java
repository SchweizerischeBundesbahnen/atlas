package ch.sbb.atlas.transport.company.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import ch.sbb.atlas.transport.company.repository.SharedTransportCompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SharedTransportCompanyConsumerTest {

  @Mock
  private SharedTransportCompanyRepository sharedTransportCompanyRepository;

  private SharedTransportCompanyConsumer sharedTransportCompanyConsumer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sharedTransportCompanyConsumer = new SharedTransportCompanyConsumer(sharedTransportCompanyRepository);
  }

  @Test
  void shouldSaveModelToDatabase() {
    // given
    SharedTransportCompanyModel transportCompany = SharedTransportCompanyModel.builder()
        .id(1L)
        .description("SBB")
        .build();

    // when
    sharedTransportCompanyConsumer.readTransportCompanyFromKafka(transportCompany);

    // then
    verify(sharedTransportCompanyRepository).save(any());
  }
}