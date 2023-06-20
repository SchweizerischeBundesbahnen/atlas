package ch.sbb.atlas.business.organisation.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.business.organisation.repository.BusinessOrganisationVersionSharingDataAccessor;
import ch.sbb.atlas.model.exception.SboidNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SharedBusinessOrganisationServiceTest {

  @Mock
  private BusinessOrganisationVersionSharingDataAccessor businessOrganisationVersionSharingDataAccessor;

  private SharedBusinessOrganisationService sharedBusinessOrganisationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    sharedBusinessOrganisationService = new SharedBusinessOrganisationService(businessOrganisationVersionSharingDataAccessor);
  }

  @Test
  void shouldThrowNotFoundExceptionOnSboidValidation() {
    when(businessOrganisationVersionSharingDataAccessor.existsBySboid(any())).thenReturn(false);

    assertThrows(SboidNotFoundException.class,
        () -> sharedBusinessOrganisationService.validateSboidExists("ch:1:sboid:12344422"));
  }

  @Test
  void shouldNotThrowOnSboidExistingValidation() {
    when(businessOrganisationVersionSharingDataAccessor.existsBySboid(any())).thenReturn(true);

    assertDoesNotThrow(() -> sharedBusinessOrganisationService.validateSboidExists("ch:1:sboid:12344422"));
  }
}