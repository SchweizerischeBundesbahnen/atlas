package ch.sbb.prm.directory.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.StopPointTestData;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.RecordingVariantException;
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
    assertThat(errorResponse.getMessage()).isEqualTo("StopPointVersion cannot be save!");
    assertThat(errorResponse.getError()).isEqualTo("Precondition failed");
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
    assertThat(errorResponse.getMessage()).isEqualTo("StopPointVersion cannot be save!");
    assertThat(errorResponse.getError()).isEqualTo("Precondition failed");
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

//  @Test
//  void shouldNotValidateWhenMeansOfTransportCombinationIsNotAllowed() {
//    //given
//    StopPointVersion stopPointVersion = StopPointTestData.getStopPointVersion();
//    stopPointVersion.setMeansOfTransport(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAIN));
//
//    //when
//    StopPointMeansOfTransportNotAllowedException result = Assertions.assertThrows(
//        StopPointMeansOfTransportNotAllowedException.class,
//        () -> stopPointValidationService.validateRecordingVariants(stopPointVersion, true));
//
//    //then
//    assertThat(result).isNotNull();
//    ErrorResponse errorResponse = result.getErrorResponse();
//    assertThat(errorResponse.getStatus()).isEqualTo(400);
//    assertThat(errorResponse.getMessage()).isEqualTo("Means of Transport combination not allowed!");
//    assertThat(errorResponse.getError()).isEqualTo("The given Means of Transport combination [BUS, TRAIN] is not allowed.\n"
//        + "Allowed combination:\n"
//        + "Reduced: [ELEVATOR, BUS, CHAIRLIFT, CABLE_CAR, CABLE_RAILWAY, BOAT, TRAM]\n"
//        + "Complete: [METRO, TRAIN, RACK_RAILWAY]");
//  }

}