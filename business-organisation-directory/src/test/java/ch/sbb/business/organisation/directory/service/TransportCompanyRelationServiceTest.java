package ch.sbb.business.organisation.directory.service;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.business.organisation.directory.entity.BusinessOrganisationVersion;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompanyRelation;
import ch.sbb.business.organisation.directory.exception.SboidNotFoundException;
import ch.sbb.business.organisation.directory.exception.TransportCompanyNotFoundException;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRelationRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class TransportCompanyRelationServiceTest {

  @Mock
  private BusinessOrganisationService businessOrganisationService;

  @Mock
  private TransportCompanyService transportCompanyService;

  @Mock
  private TransportCompanyRelationRepository transportCompanyRelationRepository;

  private AutoCloseable closeable;
  private TransportCompanyRelationService transportCompanyRelationService;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    transportCompanyRelationService = new TransportCompanyRelationService(
        transportCompanyRelationRepository, businessOrganisationService, transportCompanyService);
  }

  @AfterEach
  void cleanUp() throws Exception {
    closeable.close();
  }

  @Test
  void shouldSaveTransportCompanyRelation() {
    when(transportCompanyService.findTransportCompanyById(5L)).thenReturn(Optional.of(Mockito.mock(
        TransportCompany.class)));
    when(businessOrganisationService.findBusinessOrganisationVersions(
        "ch:1:sboid:100500")).thenReturn(
        Collections.singletonList(Mockito.mock(BusinessOrganisationVersion.class)));

    TransportCompanyRelation entity = TransportCompanyRelation.builder()
                                                              .transportCompanyId(5L)
                                                              .sboid("ch:1:sboid:100500")
                                                              .validFrom(
                                                                  LocalDate.of(2020, 1, 1))
                                                              .validTo(LocalDate.of(2021, 1, 1))
                                                              .build();

    Executable executable = () -> transportCompanyRelationService.save(entity);
    assertDoesNotThrow(executable);
    verify(transportCompanyRelationRepository, times(1)).save(entity);
  }

  @Test
  void shouldThrowExceptionWhenTransportCompanyNotExisting() {
    when(transportCompanyService.findTransportCompanyById(5L)).thenReturn(Optional.empty());
    when(businessOrganisationService.findBusinessOrganisationVersions(
        "ch:1:sboid:100500")).thenReturn(
        Collections.singletonList(Mockito.mock(BusinessOrganisationVersion.class)));

    TransportCompanyRelation entity = TransportCompanyRelation.builder()
                                                              .transportCompanyId(5L)
                                                              .sboid("ch:1:sboid:100500")
                                                              .validFrom(
                                                                  LocalDate.of(2020, 1, 1))
                                                              .validTo(LocalDate.of(2021, 1, 1))
                                                              .build();

    Executable executable = () -> transportCompanyRelationService.save(entity);
    assertThrows(TransportCompanyNotFoundException.class, executable, "Entity not found");
    verify(transportCompanyRelationRepository, times(0)).save(entity);
  }

  @Test
  void shouldThrowExceptionWhenSboidNotExists() {
    when(transportCompanyService.findTransportCompanyById(5L)).thenReturn(Optional.of(Mockito.mock(
        TransportCompany.class)));
    when(businessOrganisationService.findBusinessOrganisationVersions(
        "ch:1:sboid:100500")).thenReturn(
        Collections.emptyList());

    TransportCompanyRelation entity = TransportCompanyRelation.builder()
                                                              .transportCompanyId(5L)
                                                              .sboid("ch:1:sboid:100500")
                                                              .validFrom(
                                                                  LocalDate.of(2020, 1, 1))
                                                              .validTo(LocalDate.of(2021, 1, 1))
                                                              .build();

    Executable executable = () -> transportCompanyRelationService.save(entity);
    assertThrows(SboidNotFoundException.class, executable, "Entity not found");
    verify(transportCompanyRelationRepository, times(0)).save(entity);
  }

}
