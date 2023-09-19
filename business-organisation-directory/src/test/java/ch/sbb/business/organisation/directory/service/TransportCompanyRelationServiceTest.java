package ch.sbb.business.organisation.directory.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SboidNotFoundException;
import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import ch.sbb.business.organisation.directory.exception.TransportCompanyNotFoundException;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRelationRepository;
import java.time.LocalDate;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

 class TransportCompanyRelationServiceTest {

  @Mock
  private BusinessOrganisationService businessOrganisationService;

  @Mock
  private TransportCompanyService transportCompanyService;

  @Mock
  private TransportCompanyRelationRepository transportCompanyRelationRepository;

  private TransportCompanyRelationService transportCompanyRelationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    transportCompanyRelationService = new TransportCompanyRelationService(
        transportCompanyRelationRepository, businessOrganisationService, transportCompanyService);
  }

  @Test
  void shouldSaveTransportCompanyRelation() {
    when(transportCompanyService.existsById(5L)).thenReturn(true);
    when(businessOrganisationService.findBusinessOrganisationVersions(
        "ch:1:sboid:100500")).thenReturn(
        Collections.singletonList(Mockito.mock(BusinessOrganisationVersion.class)));

    TransportCompanyRelation entity = TransportCompanyRelation.builder()
        .transportCompany(
            TransportCompany.builder()
                .id(5L).build())
        .sboid("ch:1:sboid:100500")
        .validFrom(
            LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 1, 1))
        .build();

    Executable executable = () -> transportCompanyRelationService.save(entity, false);
    assertDoesNotThrow(executable);
    verify(transportCompanyRelationRepository, times(1)).save(entity);
  }

  @Test
  void shouldThrowExceptionWhenTransportCompanyNotExisting() {
    when(transportCompanyService.existsById(5L)).thenReturn(false);
    when(businessOrganisationService.findBusinessOrganisationVersions(
        "ch:1:sboid:100500")).thenReturn(
        Collections.singletonList(Mockito.mock(BusinessOrganisationVersion.class)));

    TransportCompanyRelation entity = TransportCompanyRelation.builder()
        .transportCompany(
            TransportCompany.builder()
                .id(5L).build())
        .sboid("ch:1:sboid:100500")
        .validFrom(
            LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 1, 1))
        .build();

    Executable executable = () -> transportCompanyRelationService.save(entity, false);
    assertThrows(TransportCompanyNotFoundException.class, executable, "Entity not found");
    verify(transportCompanyRelationRepository, times(0)).save(entity);
  }

  @Test
  void shouldThrowExceptionWhenSboidNotExists() {
    when(transportCompanyService.existsById(5L)).thenReturn(true);
    when(businessOrganisationService.findBusinessOrganisationVersions(
        "ch:1:sboid:100500")).thenReturn(
        Collections.emptyList());

    TransportCompanyRelation entity = TransportCompanyRelation.builder()
        .transportCompany(
            TransportCompany.builder()
                .id(5L).build())
        .sboid("ch:1:sboid:100500")
        .validFrom(
            LocalDate.of(2020, 1, 1))
        .validTo(LocalDate.of(2021, 1, 1))
        .build();

    Executable executable = () -> transportCompanyRelationService.save(entity, false);
    assertThrows(SboidNotFoundException.class, executable, "Entity not found");
    verify(transportCompanyRelationRepository, times(0)).save(entity);
  }

  @Test
  void shouldDelete() {
    when(transportCompanyRelationRepository.existsById(5L)).thenReturn(true);

    Executable executable = () -> transportCompanyRelationService.deleteById(5L);
    assertDoesNotThrow(executable);
    verify(transportCompanyRelationRepository, times(1)).deleteById(5L);
  }

  @Test
  void shouldThrowIdNotFoundExceptionAndNotDelete() {
    when(transportCompanyRelationRepository.existsById(5L)).thenReturn(false);

    Executable executable = () -> transportCompanyRelationService.deleteById(5L);
    assertThrows(IdNotFoundException.class, executable, "Entity not found");
    verify(transportCompanyRelationRepository, times(0)).deleteById(5L);
  }

}
