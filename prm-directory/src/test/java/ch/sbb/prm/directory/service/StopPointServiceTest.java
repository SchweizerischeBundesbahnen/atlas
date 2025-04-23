package ch.sbb.prm.directory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.ReducedVariantException;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.validation.StopPointValidationService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

class StopPointServiceTest {

  private StopPointService stopPointService;

  @Mock
  private StopPointRepository stopPointRepository;

  @Mock
  private StopPointValidationService stopPointValidationService;

  @Mock
  private VersionableService versionableService;
  @Mock
  private SharedServicePointService sharedServicePointService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    this.stopPointService = new StopPointService(stopPointRepository, versionableService,
        stopPointValidationService, sharedServicePointService);
  }

  @Test
  void shouldReturnIsReduced() {
    //given
    StopPointVersion reduced = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.BUS)).build();
    Mockito.doReturn(List.of(reduced)).when(stopPointRepository).findAllBySloidOrderByValidFrom(reduced.getSloid());
    //when
    boolean result = stopPointService.isReduced(reduced.getSloid());
    //then
    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnIsNotReduced() {
    //given
    StopPointVersion reduced = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.TRAIN)).build();
    Mockito.doReturn(List.of(reduced)).when(stopPointRepository).findAllBySloidOrderByValidFrom(reduced.getSloid());
    //when
    boolean result = stopPointService.isReduced(reduced.getSloid());
    //then
    assertThat(result).isFalse();
  }

  @Test
  void shouldThrowExceptionWhenSloidDoesNotExist() {
    //when
    StopPointDoesNotExistException result = assertThrows(
        StopPointDoesNotExistException.class,
        () -> stopPointService.isReduced("ch:1:sloid:8507000"));

    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo("The stop point with sloid ch:1:sloid:8507000 does not exist.");

  }

  @Test
  void shouldThrowExceptionWhenIsReduced() {
    //given
    StopPointVersion reduced = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.BUS)).build();
    Mockito.doReturn(List.of(reduced)).when(stopPointRepository).findAllBySloidOrderByValidFrom(reduced.getSloid());
    //when
    ReducedVariantException result = assertThrows(
        ReducedVariantException.class,
        () -> stopPointService.validateIsNotReduced(reduced.getSloid()));

    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo("Object creation not allowed for reduced variant!");
    assertThat(errorResponse.getError()).isEqualTo(
        "Only StopPoints that contain only complete mean of transports variant [[METRO, TRAIN, RACK_RAILWAY]] are allowed to "
            + "create this object.");
  }

  @Test
  void shouldNotThrowExceptionWhenIsComplete() {
    //given
    StopPointVersion reduced = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.TRAIN)).build();
    Mockito.doReturn(List.of(reduced)).when(stopPointRepository).findAllBySloidOrderByValidFrom(reduced.getSloid());
    //when
    Executable executable = () -> stopPointService.validateIsNotReduced(reduced.getSloid());

    //then
    assertDoesNotThrow(executable);
  }

  @Test
  void testCheckStopPointExists_Exists() {
    String sloid = "ch:1:sloid:12345";
    when(stopPointRepository.existsBySloid(sloid)).thenReturn(true);

    stopPointService.checkStopPointExists(sloid);
  }

  @Test
  void testCheckStopPointExists_DoesNotExist() {
    String sloid = "ch:1:sloid:12345";
    when(stopPointRepository.existsBySloid(sloid)).thenReturn(false);

    assertThrows(StopPointDoesNotExistException.class, () -> stopPointService.checkStopPointExists(sloid));
  }

  @Test
  void shouldReturnAllMeansOfTransport() {
    //given
    StopPointVersion firstVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.TRAM)).build();
    StopPointVersion secondVersion = StopPointTestData.builderVersion1().meansOfTransport(Set.of(MeanOfTransport.BOAT)).build();
    Mockito.doReturn(List.of(firstVersion, secondVersion)).when(stopPointRepository)
        .findAllBySloidOrderByValidFrom(firstVersion.getSloid());

    //when
    Set<MeanOfTransport> meanOfTransports = stopPointService.getMeansOfTransportOfAllVersions(
        firstVersion.getSloid());

    //then
    assertThat(meanOfTransports).containsExactly(MeanOfTransport.TRAM, MeanOfTransport.BOAT);
  }

}