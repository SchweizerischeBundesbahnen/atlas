package ch.sbb.business.organisation.directory.service;

import static org.mockito.Mockito.verify;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class BusinessOrganisationVersionServiceTest {

  private BusinessOrganisationVersionService service;

  @Mock
  private BusinessOrganisationVersionRepository repository;

  @Mock
  private VersionableService versionableService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new BusinessOrganisationVersionService(repository, versionableService);
  }

  @Test
  public void shouldDeleteById() {
    //given
    BusinessOrganisationVersion version = new BusinessOrganisationVersion();

    Mockito.when(repository.findById(123l)).thenReturn(Optional.ofNullable(version));
    service.deleteById(123);
    //then
    verify(repository).deleteById(123l);
  }

}