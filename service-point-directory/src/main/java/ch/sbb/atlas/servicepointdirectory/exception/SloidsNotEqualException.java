package ch.sbb.atlas.servicepointdirectory.exception;

import ch.sbb.atlas.api.model.ErrorResponse;
import ch.sbb.atlas.api.model.ErrorResponse.Detail;
import ch.sbb.atlas.api.model.ErrorResponse.DisplayInfo;
import ch.sbb.atlas.model.exception.AtlasException;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SloidsNotEqualException extends AtlasException {

  private static final String TRAFFIC_POINT_SLOIDS_NOT_EQUAL_CODE = "SEPODI.TRAFFIC_POINT_ELEMENTS.SLOIDS_NOT_EQUAL_ERROR";
  private static final String SECTOR_GROUP_TRAFFIC_POINT_SLOID_NOT_EQUAL_CODE =
      "SEPODI.SECTOR_GROUPS.TRAFFIC_POINT_SLOID_NOT_EQUAL_ERROR";

  private final String errorMessage;
  private final DisplayInfo displayInfo;

  public static SloidsNotEqualException trafficPointSloidsNotEqual(String sloid, String requestSloid) {
    return new SloidsNotEqualException(
        "Sloid for provided id: " + sloid + " and sloid in the request body: " + requestSloid + " are not equal.",
        DisplayInfo.builder()
            .code(TRAFFIC_POINT_SLOIDS_NOT_EQUAL_CODE)
            .with("sloid", sloid)
            .with("requestSloid", requestSloid)
            .build());
  }

  public static SloidsNotEqualException sectorGroupTrafficPointSloidNotEqual(String sectorTrafficPointSloid,
      String sectorGroupTrafficPointSloid) {
    return new SloidsNotEqualException(
        "Traffic Point sloid of sector not matching with sector group traffic point sloid",
        DisplayInfo.builder()
            .code(SECTOR_GROUP_TRAFFIC_POINT_SLOID_NOT_EQUAL_CODE)
            .with("sectorTrafficPointSloid", sectorTrafficPointSloid)
            .with("sectorGroupTrafficPointSloid", sectorGroupTrafficPointSloid)
            .build());
  }

  @Override
  public ErrorResponse getErrorResponse() {
    return ErrorResponse.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(errorMessage)
        .details(getDetails())
        .build();
  }

  private SortedSet<Detail> getDetails() {
    TreeSet<Detail> errorDetails = new TreeSet<>();
    errorDetails.add(Detail.builder()
        .message(errorMessage)
        .displayInfo(displayInfo)
        .build());
    return errorDetails;
  }

}
