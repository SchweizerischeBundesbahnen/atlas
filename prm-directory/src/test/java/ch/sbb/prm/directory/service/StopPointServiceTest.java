package ch.sbb.prm.directory.service;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.ReducedVariantException;
import ch.sbb.prm.directory.exception.StopPointDoesNotExistException;
import ch.sbb.prm.directory.repository.StopPointRepository;
import ch.sbb.prm.directory.validation.StopPointValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StopPointServiceTest {

  private StopPointService stopPointService;

  @Mock
  private StopPointRepository stopPointRepository;

  @Mock
  private StopPointValidationService stopPointValidationService;

  @Mock
  private VersionableService versionableService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    this.stopPointService = new StopPointService(stopPointRepository, versionableService,
        stopPointValidationService);
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
    assertThat(errorResponse.getMessage()).isEqualTo("The stop place with sloid ch:1:sloid:8507000 does not exist.");

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

}