package ch.sbb.prm.directory.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.RecordingVariantException;
import ch.sbb.prm.directory.exception.StopPointVariantChangingNotAllowedException;
import java.util.Set;
import java.util.SortedSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.MockitoAnnotations;

class StopPointValidationServiceTest {

  private StopPointValidationService stopPointValidationService;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    this.stopPointValidationService = new StopPointValidationService();
  }

  @Test
  void shouldNotValidateWhenReducedPRMContainsAllFields() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS));

    //when
    RecordingVariantException result = Assertions.assertThrows(
        RecordingVariantException.class,
        () -> stopPointValidationService.validateStopPointRecordingVariants(stopPointVersion));

    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "StopPointVersion with sloid [ch:1:sloid:12345] cannot be save: Attempting to save a Reduced object with wrong properties population!");
    assertThat(errorResponse.getDetails()).hasSize(19);
  }

  @Test
  void shouldNotValidateWhenCompletePRMDoesNotContainsAllMandatoryFields() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.TRAIN));
    stopPointVersion.setAlternativeTransport(null);

    //when
    RecordingVariantException result = Assertions.assertThrows(
        RecordingVariantException.class,
        () -> stopPointValidationService.validateStopPointRecordingVariants(stopPointVersion));

    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "StopPointVersion with sloid [ch:1:sloid:12345] cannot be save: Attempting to save a Complete object with wrong properties population!");
    SortedSet<Detail> errorResponseDetails = errorResponse.getDetails();
    assertThat(errorResponseDetails).hasSize(1);
    Detail detail = errorResponseDetails.stream().toList().get(0);
    assertThat(detail.getMessage()).isEqualTo("Must not be null for Completed Object. At least a default value is mandatory");
    assertThat(detail.getField()).isEqualTo("alternativeTransport");
  }

  @Test
  void shouldValidateWhenCompleteContainsAllFields() {
    //given
    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.TRAIN));

    //when
    Executable executable = () -> stopPointValidationService.validateStopPointRecordingVariants(stopPointVersion);

    //then
    assertDoesNotThrow(executable);
  }

  @Test
  void shouldNotValidateWhenChangingFromCompleteToReduced() {
    //given
    StopPointVersion stopPointVersionComplete = StopPointTestData.builderVersion1()
        .meansOfTransport(Set.of(MeanOfTransport.TRAIN))
        .build();
    StopPointVersion stopPointVersionReduced = StopPointTestData.builderVersion1()
        .meansOfTransport(Set.of(MeanOfTransport.TRAM))
        .build();
    //when
    StopPointVariantChangingNotAllowedException result = Assertions.assertThrows(
        StopPointVariantChangingNotAllowedException.class,
        () -> stopPointValidationService.validateMeansOfTransportChanging(stopPointVersionComplete, stopPointVersionReduced));
    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "Changing from Complete to Reduced not allowed! Allowed means of transport: [METRO, TRAIN, RACK_RAILWAY]");
  }

  @Test
  void shouldNotValidateWhenChangingFromReducedToComplete() {
    //given
    StopPointVersion stopPointVersionComplete = StopPointTestData.builderVersion1()
        .meansOfTransport(Set.of(MeanOfTransport.TRAM))
        .build();
    StopPointVersion stopPointVersionReduced = StopPointTestData.builderVersion1()
        .meansOfTransport(Set.of(MeanOfTransport.TRAIN))
        .build();
    //when
    StopPointVariantChangingNotAllowedException result = Assertions.assertThrows(
        StopPointVariantChangingNotAllowedException.class,
        () -> stopPointValidationService.validateMeansOfTransportChanging(stopPointVersionComplete, stopPointVersionReduced));
    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo(
        "Changing from Reduced to Complete not allowed! Allowed means of transport: [ELEVATOR, BUS, CHAIRLIFT, CABLE_CAR, "
            + "CABLE_RAILWAY, BOAT, TRAM]");
  }

  @Test
  void shouldValidateWhenChangingFromReducedToReduced() {
    //given
    StopPointVersion stopPointVersionComplete = StopPointTestData.builderVersion1()
        .meansOfTransport(Set.of(MeanOfTransport.TRAM))
        .build();
    StopPointVersion stopPointVersionReduced = StopPointTestData.builderVersion1()
        .meansOfTransport(Set.of(MeanOfTransport.BUS))
        .build();

    //when
    Executable executable = () -> stopPointValidationService.validateMeansOfTransportChanging(stopPointVersionComplete,
        stopPointVersionReduced);

    //then
    assertDoesNotThrow(executable);
  }

  @Test
  void shouldValidateWhenChangingFromCompleteToComplete() {
    //given
    StopPointVersion stopPointVersionComplete = StopPointTestData.builderVersion1()
        .meansOfTransport(Set.of(MeanOfTransport.TRAIN))
        .build();
    StopPointVersion stopPointVersionReduced = StopPointTestData.builderVersion1()
        .meansOfTransport(Set.of(MeanOfTransport.RACK_RAILWAY))
        .build();

    //when
    Executable executable = () -> stopPointValidationService.validateMeansOfTransportChanging(stopPointVersionComplete,
        stopPointVersionReduced);

    //then
    assertDoesNotThrow(executable);
  }

}