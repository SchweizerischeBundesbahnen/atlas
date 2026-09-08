package ch.sbb.atlas.servicepointdirectory.module.bulkimport.servicepoint.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.imports.bulk.BulkImportUpdateContainer;
import ch.sbb.atlas.imports.model.ServicePointGlobalIdUpdateCsvModel;
import ch.sbb.atlas.imports.model.ServicePointGlobalIdUpdateCsvModel.Fields;
import ch.sbb.atlas.model.exception.SloidNotFoundException;
import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.module.bulkimport.servicepoint.exception.GlobalIdBulkImportInfoException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.entity.ServicePointVersion;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointIdentifierMismatchException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.exception.ServicePointNumberNotFoundException;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.model.GlobalId;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.GlobalIdService;
import ch.sbb.atlas.servicepointdirectory.module.servicepoint.service.ServicePointService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Pins the concurrency handling of the Global-ID bulk import, which cannot be provoked through the integration test because it
 * requires the repoint to fail on the first attempt.
 */
@ExtendWith(MockitoExtension.class)
class ServicePointGlobalIdBulkImportRetryTest {

  private static final Integer NUMBER_WITHOUT_CHECK_DIGIT = 1105770;
  private static final ServicePointNumber SERVICE_POINT_NUMBER = ServicePointNumber.ofNumberWithoutCheckDigit(
      NUMBER_WITHOUT_CHECK_DIGIT);
  private static final String SLOID = "ch:1:sloid:5770";
  private static final String GLOBAL_ID = "de:05770:1282";

  @Mock
  private ServicePointService servicePointService;

  @Mock
  private GlobalIdService globalIdService;

  @InjectMocks
  private ServicePointBulkImportService servicePointBulkImportService;

  private static ServicePointVersion servicePointVersion() {
    return ServicePointVersion.builder()
        .number(SERVICE_POINT_NUMBER)
        .sloid(SLOID)
        .country(Country.GERMANY_BUS)
        .build();
  }

  private static BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> container(String sloid, Integer number,
      String globalId) {
    return BulkImportUpdateContainer.<ServicePointGlobalIdUpdateCsvModel>builder()
        .object(ServicePointGlobalIdUpdateCsvModel.builder().sloid(sloid).number(number).globalId(globalId).build())
        .attributesToNull(List.of())
        .build();
  }

  @Test
  void shouldRetryTheRepointOnceWhenTheUniqueIndexWasHitByAConcurrentImport() {
    // Given
    when(servicePointService.findAllByNumberOrderByValidFrom(SERVICE_POINT_NUMBER)).thenReturn(
        List.of(servicePointVersion()));
    when(globalIdService.saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class)))
        .thenThrow(new DataIntegrityViolationException("unique index"))
        .thenReturn(Optional.empty());

    // When
    servicePointBulkImportService.updateGlobalId(container(null, NUMBER_WITHOUT_CHECK_DIGIT, GLOBAL_ID));

    // Then
    verify(globalIdService, times(2)).saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class));
  }

  @Test
  void shouldRetryTheRepointOnceAfterAConcurrencyFailure() {
    // Given
    when(servicePointService.findAllByNumberOrderByValidFrom(SERVICE_POINT_NUMBER)).thenReturn(
        List.of(servicePointVersion()));
    when(globalIdService.saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class)))
        .thenThrow(new ConcurrencyFailureException("optimistic lock"))
        .thenReturn(Optional.empty());

    // When
    servicePointBulkImportService.updateGlobalId(container(null, NUMBER_WITHOUT_CHECK_DIGIT, GLOBAL_ID));

    // Then
    verify(globalIdService, times(2)).saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class));
  }

  @Test
  void shouldReportTheDisplacedServicePointFoundByTheRetry() {
    // Given
    ServicePointNumber displaced = ServicePointNumber.ofNumberWithoutCheckDigit(1105771);
    when(servicePointService.findAllByNumberOrderByValidFrom(SERVICE_POINT_NUMBER)).thenReturn(
        List.of(servicePointVersion()));
    when(globalIdService.saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class)))
        .thenThrow(new DataIntegrityViolationException("unique index"))
        .thenReturn(Optional.of(displaced));

    // When
    GlobalIdBulkImportInfoException info = catchThrowableOfType(GlobalIdBulkImportInfoException.class,
        () -> servicePointBulkImportService.updateGlobalId(container(null, NUMBER_WITHOUT_CHECK_DIGIT, GLOBAL_ID)));

    // Then
    assertThat(info.getCode()).isEqualTo("SEPODI.SERVICE_POINTS.GLOBAL_ID_INFO.REPOINTED");
  }

  @Test
  void shouldGiveUpWhenTheRetryFailsAsWell() {
    // Given
    when(servicePointService.findAllByNumberOrderByValidFrom(SERVICE_POINT_NUMBER)).thenReturn(
        List.of(servicePointVersion()));
    when(globalIdService.saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class)))
        .thenThrow(new DataIntegrityViolationException("unique index"));

    BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> container = container(null, NUMBER_WITHOUT_CHECK_DIGIT,
        GLOBAL_ID);

    // When & Then
    assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(
        () -> servicePointBulkImportService.updateGlobalId(container));

    verify(globalIdService, times(2)).saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class));
  }

  @Test
  void shouldNotRetryOnAnUnrelatedFailure() {
    // Given
    when(servicePointService.findAllByNumberOrderByValidFrom(SERVICE_POINT_NUMBER)).thenReturn(
        List.of(servicePointVersion()));
    when(globalIdService.saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class)))
        .thenThrow(new IllegalStateException("unrelated"));

    BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> container = container(null, NUMBER_WITHOUT_CHECK_DIGIT,
        GLOBAL_ID);

    // When & Then
    assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
        () -> servicePointBulkImportService.updateGlobalId(container));

    verify(globalIdService, times(1)).saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class));
  }

  @Test
  void shouldResolveTheServicePointBySloidWhenNoNumberIsGiven() {
    // Given
    when(servicePointService.findBySloidAndOrderByValidFrom(SLOID)).thenReturn(List.of(servicePointVersion()));
    when(globalIdService.saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class))).thenReturn(Optional.empty());

    // When
    servicePointBulkImportService.updateGlobalId(container(SLOID, null, GLOBAL_ID));

    // Then
    verify(servicePointService).findBySloidAndOrderByValidFrom(SLOID);
  }

  @Test
  void shouldFailWhenTheSloidIsUnknown() {
    // Given
    when(servicePointService.findBySloidAndOrderByValidFrom(SLOID)).thenReturn(List.of());

    BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> container = container(SLOID, null, GLOBAL_ID);

    // When & Then
    assertThatExceptionOfType(SloidNotFoundException.class).isThrownBy(
        () -> servicePointBulkImportService.updateGlobalId(container));
  }

  @Test
  void shouldFailWhenTheServicePointNumberIsUnknown() {
    // Given
    when(servicePointService.findAllByNumberOrderByValidFrom(SERVICE_POINT_NUMBER)).thenReturn(List.of());

    BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> container = container(null, NUMBER_WITHOUT_CHECK_DIGIT,
        GLOBAL_ID);

    // When & Then
    assertThatExceptionOfType(ServicePointNumberNotFoundException.class).isThrownBy(
        () -> servicePointBulkImportService.updateGlobalId(container));
  }

  @Test
  void shouldNotTouchTheMappingWhenSloidAndNumberDisagree() {
    // Given
    when(servicePointService.findAllByNumberOrderByValidFrom(SERVICE_POINT_NUMBER)).thenReturn(
        List.of(servicePointVersion()));

    BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> container = container("ch:1:sloid:9999",
        NUMBER_WITHOUT_CHECK_DIGIT, GLOBAL_ID);

    // When & Then
    assertThatExceptionOfType(ServicePointIdentifierMismatchException.class).isThrownBy(
        () -> servicePointBulkImportService.updateGlobalId(container));

    verifyNoInteractions(globalIdService);
  }

  @Test
  void shouldUseTheLastVersionToDetermineTheServicePointNumber() {
    // Given
    ServicePointVersion older = servicePointVersion();
    ServicePointVersion latest = servicePointVersion();
    when(servicePointService.findAllByNumberOrderByValidFrom(SERVICE_POINT_NUMBER)).thenReturn(List.of(older, latest));
    when(globalIdService.saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class))).thenReturn(Optional.empty());

    // When
    servicePointBulkImportService.updateGlobalId(container(null, NUMBER_WITHOUT_CHECK_DIGIT, GLOBAL_ID));

    // Then
    verify(globalIdService).saveWithRepoint(eq(SERVICE_POINT_NUMBER), any(GlobalId.class));
  }

  @Test
  void shouldRemoveTheMappingWhenTheGlobalIdColumnIsExplicitlyNulled() {
    // Given
    when(servicePointService.findAllByNumberOrderByValidFrom(SERVICE_POINT_NUMBER)).thenReturn(
        List.of(servicePointVersion()));
    when(globalIdService.removeAndGet(SERVICE_POINT_NUMBER)).thenReturn(Optional.empty());

    BulkImportUpdateContainer<ServicePointGlobalIdUpdateCsvModel> container =
        BulkImportUpdateContainer.<ServicePointGlobalIdUpdateCsvModel>builder()
            .object(ServicePointGlobalIdUpdateCsvModel.builder().number(NUMBER_WITHOUT_CHECK_DIGIT).build())
            .attributesToNull(List.of(Fields.globalId))
            .build();

    // When
    servicePointBulkImportService.updateGlobalId(container);

    // Then
    verify(globalIdService).removeAndGet(SERVICE_POINT_NUMBER);
    verify(globalIdService, times(0)).saveWithRepoint(any(), any());
  }
}
