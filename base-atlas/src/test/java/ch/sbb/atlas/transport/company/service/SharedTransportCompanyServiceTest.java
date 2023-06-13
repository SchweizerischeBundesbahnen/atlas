package ch.sbb.atlas.transport.company.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.transport.company.entity.SharedTransportCompany;
import ch.sbb.atlas.transport.company.repository.SharedTransportCompanyRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SharedTransportCompanyServiceTest {

  @Mock
  private SharedTransportCompanyRepository sharedTransportCompanyRepository;

  private SharedTransportCompanyService sharedTransportCompanyService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sharedTransportCompanyService = new SharedTransportCompanyService(sharedTransportCompanyRepository);
  }

  @Test
  void shouldRetrieveFoundEntityFromRepository() {
    when(sharedTransportCompanyRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<SharedTransportCompany> result = sharedTransportCompanyService.findById(1L);
    assertThat(result).isEmpty();
    verify(sharedTransportCompanyRepository).findById(1L);
  }
}