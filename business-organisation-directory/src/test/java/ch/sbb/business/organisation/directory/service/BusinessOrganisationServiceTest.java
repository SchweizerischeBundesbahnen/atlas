package ch.sbb.business.organisation.directory.service;

import static org.mockito.Mockito.verify;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationRepository;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.util.Collections;
import java.util.List;
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

  @Mock
  private BusinessOrganisationValidationService validationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new BusinessOrganisationService(versionRepository, repository, versionableService,
        validationService);
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

  @Test
  public void shouldDeleteByList() {
    //given
    BusinessOrganisationVersion version = new BusinessOrganisationVersion();

    List<BusinessOrganisationVersion> versions = Collections.singletonList(version);
    service.deleteAll(versions);
    //then
    verify(versionRepository).deleteAll(versions);
  }

}