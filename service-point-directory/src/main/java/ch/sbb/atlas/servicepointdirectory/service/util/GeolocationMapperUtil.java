package ch.sbb.atlas.servicepointdirectory.service.util;

import ch.sbb.atlas.servicepointdirectory.enumeration.SpatialReference;
import lombok.experimental.UtilityClass;

@UtilityClass
public class GeolocationMapperUtil {

  private static final int WGS84_EAST_MAX = 180;
  private static final int WGS84_NORTH_MAX = 90;

  public Double getOriginalEast(
      SpatialReference spatialReference,
      Double wgs84,
      Double wgs84Web,
      Double lv95,
      Double lv03) {
    if (wgs84 == null || wgs84 < -WGS84_EAST_MAX || wgs84 > WGS84_EAST_MAX) {
      return null;
    }
    return getCoordinateBySpatialReference(spatialReference, wgs84, wgs84Web, lv95, lv03);
  }

  public Double getOriginalNorth(
      SpatialReference spatialReference,
      Double wgs84,
      Double wgs84Web,
      Double lv95,
      Double lv03) {
    if (wgs84 == null || wgs84 < -WGS84_NORTH_MAX || wgs84 > WGS84_NORTH_MAX) {
      return null;
    }
    return getCoordinateBySpatialReference(spatialReference, wgs84, wgs84Web, lv95, lv03);
  }

  private static Double getCoordinateBySpatialReference(SpatialReference spatialReference,
      Double wgs84, Double wgs84Web,
      Double lv95, Double lv03) {
    return switch (spatialReference) {
      case WGS84WEB -> wgs84Web;
      case LV95 -> lv95;
      case LV03 -> lv03;
      case WGS84 -> wgs84;
      default -> null;
    };
  }
}
