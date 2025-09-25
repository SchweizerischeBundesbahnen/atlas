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
public class SectorNotExistingException extends AtlasException {

  private final String sloid;

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .message("No sector exists with sloid " + sloid)
        .details(getDetails())
        .build();
  }

  private SortedSet<Detail> getDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .message("Sector does not exist.")
        .displayInfo(DisplayInfo.builder()
            .code("SEPODI.SECTORS.SECTOR_NOT_EXISTING_ERROR")
            .with("sloid", sloid)
            .build())
        .build());
    return errorDetails;
  }
}
