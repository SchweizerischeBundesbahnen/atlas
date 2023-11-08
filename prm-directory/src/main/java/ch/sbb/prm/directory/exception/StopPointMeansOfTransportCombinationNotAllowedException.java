package ch.sbb.prm.directory.exception;

import static ch.sbb.prm.directory.validation.PrmMeansOfTransportHelper.COMPLETE_MEANS_OF_TRANSPORT;
import static ch.sbb.prm.directory.validation.PrmMeansOfTransportHelper.REDUCED_MEANS_OF_TRANSPORT;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.model.exception.AtlasException;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.util.Comparator;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class StopPointMeansOfTransportCombinationNotAllowedException extends AtlasException {

  private final Set<MeanOfTransport> meanOfTransports;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Means of Transport combination not allowed!")
        .error(""" 
            The given Means of Transport combination %s is not allowed.
            Allowed combination:
            Reduced: %s
            Complete: %s"""
            .formatted(meanOfTransports.stream().sorted(Comparator.comparing(MeanOfTransport::getName)).toList().toString(),
            REDUCED_MEANS_OF_TRANSPORT,
            COMPLETE_MEANS_OF_TRANSPORT))
        .build();
  }
}
