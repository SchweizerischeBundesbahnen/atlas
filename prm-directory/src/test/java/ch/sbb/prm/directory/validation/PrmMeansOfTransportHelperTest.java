package ch.sbb.prm.directory.validation;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.exception.StopPointMeansOfTransportCombinationNotAllowedException;
import ch.sbb.prm.directory.exception.UnknownMeanOfTransportNotAllowedException;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class PrmMeansOfTransportHelperTest {

  @Test
  void shouldNotValidateWhenMeansOfTransportCombinationIsNotAllowed() {
    //when
    StopPointMeansOfTransportCombinationNotAllowedException result = assertThrows(
        StopPointMeansOfTransportCombinationNotAllowedException.class,
        () -> PrmMeansOfTransportHelper.isReduced(Set.of(MeanOfTransport.BUS, MeanOfTransport.TRAIN)));

    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(400);
    assertThat(errorResponse.getMessage()).isEqualTo("Means of Transport combination not allowed!");
    assertThat(errorResponse.getError()).isEqualTo("The given Means of Transport combination [BUS, TRAIN] is not allowed.\n"
        + "Allowed combination:\n"
        + "Reduced: [ELEVATOR, BUS, CHAIRLIFT, CABLE_CAR, CABLE_RAILWAY, BOAT, TRAM]\n"
        + "Complete: [METRO, TRAIN, RACK_RAILWAY]");
  }

  @Test
  void shouldNotValidateWhenMeansOfTransportIsUnknown() {
    //when
    UnknownMeanOfTransportNotAllowedException result = assertThrows(
        UnknownMeanOfTransportNotAllowedException.class,
        () -> PrmMeansOfTransportHelper.isReduced(Set.of(MeanOfTransport.UNKNOWN, MeanOfTransport.TRAIN)));

    //then
    assertThat(result).isNotNull();
    ErrorResponse errorResponse = result.getErrorResponse();
    assertThat(errorResponse.getStatus()).isEqualTo(412);
    assertThat(errorResponse.getMessage()).isEqualTo("Mean of transport [UNKNOWN] not allowed for StopPoint!");
  }

  @Test
  void shouldValidateWhenMeansOfTransportCompleteCombinationIsAllowed() {
    //when
    Executable executable = () -> PrmMeansOfTransportHelper.isReduced(
        Set.of(MeanOfTransport.TRAIN, MeanOfTransport.RACK_RAILWAY));

    //then
    assertDoesNotThrow(executable);
  }

  @Test
  void shouldValidateWhenMeansOfTransportReducedCombinationIsAllowed() {
    //when
    Executable executable = () -> PrmMeansOfTransportHelper.isReduced(Set.of(MeanOfTransport.BOAT, MeanOfTransport.BUS));

    //then
    assertDoesNotThrow(executable);
  }

  @Test
  void shouldCheckIsReduced() {
    //when
    PrmMeansOfTransportHelper.REDUCED_MEANS_OF_TRANSPORT.forEach(meanOfTransport ->
        assertThat(PrmMeansOfTransportHelper.isReduced(Set.of(meanOfTransport))).isTrue());
  }

  @Test
  void shouldCheckIsComplete() {
    //when
    PrmMeansOfTransportHelper.COMPLETE_MEANS_OF_TRANSPORT.forEach(meanOfTransport ->
        assertThat(PrmMeansOfTransportHelper.isReduced(Set.of(meanOfTransport))).isFalse());
  }

}