package ch.sbb.business.organisation.directory.service;

import static org.mockito.Mockito.verify;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationRepository;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class BusinessOrganisationServiceTest {

  private BusinessOrganisationService service;

  @Mock
  private BusinessOrganisationVersionRepository versionRepository;
  @Mock
  private BusinessOrganisationRepository repository;

  @Mock
  private VersionableService versionableService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new BusinessOrganisationService(versionRepository, repository, versionableService);
  }

  @Test
  public void shouldDeleteById() {
    //given
    BusinessOrganisationVersion version = new BusinessOrganisationVersion();

    Mockito.when(versionRepository.findById(123L)).thenReturn(Optional.of(version));
    service.deleteById(123);
    //then
    verify(versionRepository).deleteById(123L);
  }

}