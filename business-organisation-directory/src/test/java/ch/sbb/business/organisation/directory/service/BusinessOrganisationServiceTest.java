package ch.sbb.business.organisation.directory.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion.BusinessOrganisationVersionBuilder;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationRepository;
import ch.sbb.business.organisation.directory.repository.BusinessOrganisationVersionRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

 class BusinessOrganisationServiceTest {

  private BusinessOrganisationService service;

  @Mock
  private BusinessOrganisationVersionRepository versionRepository;
  @Mock
  private BusinessOrganisationRepository repository;

  @Mock
  private VersionableService versionableService;

  @Mock
  private BusinessOrganisationValidationService validationService;

  @Mock
  private BusinessOrganisationDistributor businessOrganisationDistributor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new BusinessOrganisationService(versionRepository, repository, versionableService,
        validationService, businessOrganisationDistributor);
  }

  @Test
   void shouldDeleteById() {
    //given
    BusinessOrganisationVersion version = new BusinessOrganisationVersion();

    Mockito.when(versionRepository.findById(123L)).thenReturn(Optional.of(version));
    service.deleteById(123);
    //then
    verify(versionRepository).deleteById(123L);
  }

  @Test
   void shouldDeleteByList() {
    //given
    BusinessOrganisationVersion version = new BusinessOrganisationVersion();

    List<BusinessOrganisationVersion> versions = Collections.singletonList(version);
    service.deleteAll(versions);
    //then
    verify(versionRepository).deleteAll(versions);
  }

  @Test
   void shouldThrowStaleExceptionOnDifferentVersion() {
    //given
    BusinessOrganisationVersionBuilder<?, ?> version = BusinessOrganisationVersion.builder()
                                                                                  .sboid(
                                                                                      "sboid");

    Executable executable = () -> service.updateBusinessOrganisationVersion(
        version.version(1).build(), version.version(0).build());
    assertThrows(StaleObjectStateException.class, executable);
    //then
    verify(versionRepository).incrementVersion("sboid");
  }

}