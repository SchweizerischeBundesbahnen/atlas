package ch.sbb.atlas.transport.company.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.kafka.model.transport.company.SharedTransportCompanyModel;
import ch.sbb.atlas.transport.company.repository.TransportCompanySharingDataAccessor;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SharedTransportCompanyServiceTest {

  @Mock
  private TransportCompanySharingDataAccessor transportCompanySharingDataAccessor;

  private SharedTransportCompanyService sharedTransportCompanyService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sharedTransportCompanyService = new SharedTransportCompanyService(transportCompanySharingDataAccessor);
  }

  @Test
  void shouldRetrieveFoundEntityFromRepository() {
    when(transportCompanySharingDataAccessor.findTransportCompanyById(1L)).thenReturn(Optional.empty());

    Optional<SharedTransportCompanyModel> result = sharedTransportCompanyService.findById(1L);
    assertThat(result).isEmpty();
    verify(transportCompanySharingDataAccessor).findTransportCompanyById(1L);
  }
}