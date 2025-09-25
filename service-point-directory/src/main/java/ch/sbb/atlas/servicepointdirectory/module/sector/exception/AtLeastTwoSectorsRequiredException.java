package ch.sbb.atlas.servicepointdirectory.module.sector.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class AtLeastTwoSectorsRequiredException extends AtlasException {

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("At least two sector's are required")
        .details(getDetails())
        .build();
  }

  private SortedSet<Detail> getDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .message("At least two sector's are required.")
        .displayInfo(DisplayInfo.builder()
            .code("SEPODI.SECTORS.REQUIRED_AT_LEAST_TWO_SECTOR_ERROR")
            .build())
        .build());
    return errorDetails;
  }
}
