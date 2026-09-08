package ch.sbb.atlas.servicepointdirectory.module.servicepoint.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.servicepoint.ReadServicePointVersionModel;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointGlobalId;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.InvalidGlobalIdException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.model.GlobalId;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.repository.ServicePointGlobalIdRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class GlobalIdServiceTest {

  private static final ServicePointNumber GERMAN_NUMBER = ServicePointNumber.ofNumberWithoutCheckDigit(8005770);
  private static final ServicePointNumber AUSTRIAN_NUMBER = ServicePointNumber.ofNumberWithoutCheckDigit(8109379);
  private static final ServicePointNumber SWISS_NUMBER = ServicePointNumber.ofNumberWithoutCheckDigit(8507000);

  @Mock
  private ServicePointGlobalIdRepository servicePointGlobalIdRepository;

  private GlobalIdService globalIdService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    globalIdService = new GlobalIdService(servicePointGlobalIdRepository);
  }

  @Test
  void shouldEnrichSingleModelWithGlobalIdByNumber() {
    // Given
    ReadServicePointVersionModel model = ReadServicePointVersionModel.builder().number(GERMAN_NUMBER).build();
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER))
        .thenReturn(
            Optional.of(ServicePointGlobalId.builder().servicePointNumber(GERMAN_NUMBER).globalId("de:05770:1282").build()));

    // When
    globalIdService.enrich(model);

    // Then
    assertThat(model.getGlobalId()).isEqualTo("de:05770:1282");
  }

  @Test
  void shouldLeaveGlobalIdNullWhenNoMappingExistsForNumber() {
    // Given
    ReadServicePointVersionModel model = ReadServicePointVersionModel.builder().number(SWISS_NUMBER).build();
    when(servicePointGlobalIdRepository.findByServicePointNumber(SWISS_NUMBER)).thenReturn(Optional.empty());

    // When
    globalIdService.enrich(model);

    // Then
    assertThat(model.getGlobalId()).isNull();
  }

  @Test
  void shouldBatchEnrichModelsByNumberInSingleRepositoryCall() {
    // Given
    ReadServicePointVersionModel german = ReadServicePointVersionModel.builder().number(GERMAN_NUMBER).build();
    ReadServicePointVersionModel austrian = ReadServicePointVersionModel.builder().number(AUSTRIAN_NUMBER).build();
    ReadServicePointVersionModel swiss = ReadServicePointVersionModel.builder().number(SWISS_NUMBER).build();

    when(servicePointGlobalIdRepository.findByServicePointNumberIn(anyCollection())).thenReturn(List.of(
        ServicePointGlobalId.builder().servicePointNumber(GERMAN_NUMBER).globalId("de:05770:1282").build(),
        ServicePointGlobalId.builder().servicePointNumber(AUSTRIAN_NUMBER).globalId("at:42:9379").build()));

    // When
    globalIdService.enrich(List.of(german, austrian, swiss));

    // Then
    assertThat(german.getGlobalId()).isEqualTo("de:05770:1282");
    assertThat(austrian.getGlobalId()).isEqualTo("at:42:9379");
    assertThat(swiss.getGlobalId()).isNull();
    verify(servicePointGlobalIdRepository).findByServicePointNumberIn(anyCollection());
  }

  @Test
  void shouldRejectGlobalIdAlreadyUsedByAnotherStop() {
    // Given
    when(servicePointGlobalIdRepository.findByGlobalId("de:05770:1282")).thenReturn(
        Optional.of(ServicePointGlobalId.builder().servicePointNumber(AUSTRIAN_NUMBER).globalId("de:05770:1282").build()));
    GlobalId globalId = GlobalId.of("de:05770:1282", Country.GERMANY);

    // When / Then
    assertThatThrownBy(() -> globalIdService.validateUniqueness(GERMAN_NUMBER, globalId))
        .isInstanceOf(InvalidGlobalIdException.class)
        .satisfies(e -> assertThat(((InvalidGlobalIdException) e).getCode())
            .isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_ERROR.ALREADY_USED"));
  }

  @Test
  void shouldAllowSameGlobalIdOnSameStop() {
    // Given
    when(servicePointGlobalIdRepository.findByGlobalId("de:05770:1282")).thenReturn(
        Optional.of(ServicePointGlobalId.builder().servicePointNumber(GERMAN_NUMBER).globalId("de:05770:1282").build()));

    // When / Then
    globalIdService.validateUniqueness(GERMAN_NUMBER, GlobalId.of("de:05770:1282", Country.GERMANY));
  }

  @Test
  void shouldPersistNewMappingOnSave() {
    // Given
    when(servicePointGlobalIdRepository.findByGlobalId("de:05770:1282")).thenReturn(Optional.empty());
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER)).thenReturn(Optional.empty());

    // When
    globalIdService.save(GERMAN_NUMBER, GlobalId.of("de:05770:1282", Country.GERMANY));

    // Then
    ArgumentCaptor<ServicePointGlobalId> captor = ArgumentCaptor.forClass(ServicePointGlobalId.class);
    verify(servicePointGlobalIdRepository).save(captor.capture());
    assertThat(captor.getValue().getServicePointNumber()).isEqualTo(GERMAN_NUMBER);
    assertThat(captor.getValue().getGlobalId()).isEqualTo("de:05770:1282");
  }

  @Test
  void shouldUpdateExistingMappingOnSave() {
    // Given
    ServicePointGlobalId existing = ServicePointGlobalId.builder().id(5L).servicePointNumber(GERMAN_NUMBER).globalId("de:1:1")
        .build();
    when(servicePointGlobalIdRepository.findByGlobalId("de:05770:1282")).thenReturn(Optional.empty());
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER)).thenReturn(Optional.of(existing));

    // When
    globalIdService.save(GERMAN_NUMBER, GlobalId.of("de:05770:1282", Country.GERMANY));

    // Then
    ArgumentCaptor<ServicePointGlobalId> captor = ArgumentCaptor.forClass(ServicePointGlobalId.class);
    verify(servicePointGlobalIdRepository).save(captor.capture());
    assertThat(captor.getValue().getId()).isEqualTo(5L);
    assertThat(captor.getValue().getGlobalId()).isEqualTo("de:05770:1282");
  }

  @Test
  void shouldRemoveExistingMappingOnRemove() {
    // Given
    ServicePointGlobalId existing = ServicePointGlobalId.builder().id(5L).servicePointNumber(GERMAN_NUMBER).globalId("de:1:1")
        .build();
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER)).thenReturn(Optional.of(existing));

    // When
    globalIdService.remove(GERMAN_NUMBER);

    // Then
    verify(servicePointGlobalIdRepository).delete(existing);
    verify(servicePointGlobalIdRepository, never()).save(any());
  }

  @Test
  void shouldDoNothingOnRemoveWhenNoMappingExists() {
    // Given
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER)).thenReturn(Optional.empty());

    // When
    globalIdService.remove(GERMAN_NUMBER);

    // Then
    verify(servicePointGlobalIdRepository, never()).delete(any());
  }

  @Test
  void shouldTakeGlobalIdAwayFromItsHolderAndReportItOnRepoint() {
    // Given
    ServicePointGlobalId holder = ServicePointGlobalId.builder().id(7L).servicePointNumber(AUSTRIAN_NUMBER)
        .globalId("de:05770:1282").build();
    when(servicePointGlobalIdRepository.findByGlobalId("de:05770:1282")).thenReturn(Optional.of(holder));
    when(servicePointGlobalIdRepository.releaseGlobalIdFrom("de:05770:1282", AUSTRIAN_NUMBER)).thenReturn(1);
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER)).thenReturn(Optional.empty());

    // When
    Optional<ServicePointNumber> displaced = globalIdService.saveWithRepoint(GERMAN_NUMBER,
        GlobalId.of("de:05770:1282", Country.GERMANY));

    // Then
    assertThat(displaced).contains(AUSTRIAN_NUMBER);
    ArgumentCaptor<ServicePointGlobalId> captor = ArgumentCaptor.forClass(ServicePointGlobalId.class);
    verify(servicePointGlobalIdRepository).save(captor.capture());
    assertThat(captor.getValue().getServicePointNumber()).isEqualTo(GERMAN_NUMBER);
  }

  @Test
  void shouldReleaseTheGlobalIdBeforeClaimingIt() {
    // Given
    ServicePointGlobalId holder = ServicePointGlobalId.builder().id(7L).servicePointNumber(AUSTRIAN_NUMBER)
        .globalId("de:05770:1282").build();
    when(servicePointGlobalIdRepository.findByGlobalId("de:05770:1282")).thenReturn(Optional.of(holder));
    when(servicePointGlobalIdRepository.releaseGlobalIdFrom("de:05770:1282", AUSTRIAN_NUMBER)).thenReturn(1);
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER)).thenReturn(Optional.empty());

    // When
    globalIdService.saveWithRepoint(GERMAN_NUMBER, GlobalId.of("de:05770:1282", Country.GERMANY));

    // Then
    InOrder inOrder = inOrder(servicePointGlobalIdRepository);
    inOrder.verify(servicePointGlobalIdRepository).releaseGlobalIdFrom("de:05770:1282", AUSTRIAN_NUMBER);
    inOrder.verify(servicePointGlobalIdRepository).save(any());
  }

  @Test
  void shouldNotReportADisplacementWhenTheHolderNoLongerHeldTheGlobalId() {
    // Given a holder that a concurrent transaction moved elsewhere between the read and the conditional delete
    ServicePointGlobalId holder = ServicePointGlobalId.builder().id(7L).servicePointNumber(AUSTRIAN_NUMBER)
        .globalId("de:05770:1282").build();
    when(servicePointGlobalIdRepository.findByGlobalId("de:05770:1282")).thenReturn(Optional.of(holder));
    when(servicePointGlobalIdRepository.releaseGlobalIdFrom("de:05770:1282", AUSTRIAN_NUMBER)).thenReturn(0);
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER)).thenReturn(Optional.empty());

    // When
    Optional<ServicePointNumber> displaced = globalIdService.saveWithRepoint(GERMAN_NUMBER,
        GlobalId.of("de:05770:1282", Country.GERMANY));

    // Then
    assertThat(displaced).isEmpty();
  }

  @Test
  void shouldNotDisplaceTheServicePointThatAlreadyHoldsTheGlobalId() {
    // Given
    ServicePointGlobalId holder = ServicePointGlobalId.builder().id(7L).servicePointNumber(GERMAN_NUMBER)
        .globalId("de:05770:1282").build();
    when(servicePointGlobalIdRepository.findByGlobalId("de:05770:1282")).thenReturn(Optional.of(holder));
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER)).thenReturn(Optional.of(holder));

    // When
    Optional<ServicePointNumber> displaced = globalIdService.saveWithRepoint(GERMAN_NUMBER,
        GlobalId.of("de:05770:1282", Country.GERMANY));

    // Then
    assertThat(displaced).isEmpty();
    verify(servicePointGlobalIdRepository, never()).releaseGlobalIdFrom(any(), any());
  }

  @Test
  void shouldReportNobodyDisplacedWhenTheGlobalIdIsFree() {
    // Given
    when(servicePointGlobalIdRepository.findByGlobalId("de:05770:1282")).thenReturn(Optional.empty());
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER)).thenReturn(Optional.empty());

    // When
    Optional<ServicePointNumber> displaced = globalIdService.saveWithRepoint(GERMAN_NUMBER,
        GlobalId.of("de:05770:1282", Country.GERMANY));

    // Then
    assertThat(displaced).isEmpty();
    verify(servicePointGlobalIdRepository, never()).releaseGlobalIdFrom(any(), any());
    verify(servicePointGlobalIdRepository).save(any());
  }

  @Test
  void shouldReportTheRemovedGlobalIdOnRemoveAndGet() {
    // Given
    ServicePointGlobalId existing = ServicePointGlobalId.builder().id(5L).servicePointNumber(GERMAN_NUMBER)
        .globalId("de:05770:1282").build();
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER)).thenReturn(Optional.of(existing));

    // When / Then
    assertThat(globalIdService.removeAndGet(GERMAN_NUMBER)).contains("de:05770:1282");
    verify(servicePointGlobalIdRepository).delete(existing);
  }

  @Test
  void shouldReportNothingRemovedWhenNoMappingExists() {
    // Given
    when(servicePointGlobalIdRepository.findByServicePointNumber(GERMAN_NUMBER)).thenReturn(Optional.empty());

    // When / Then
    assertThat(globalIdService.removeAndGet(GERMAN_NUMBER)).isEmpty();
  }
}
