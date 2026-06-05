package ch.sbb.atlas.imports.bulk.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(enumAsRef = true)
public enum BusinessObjectType {

  SERVICE_POINT,
  TRAFFIC_POINT,
  LOADING_POINT,
  SECTOR,

  STOP_POINT,
  PLATFORM,
  PLATFORM_REDUCED,
  PLATFORM_COMPLETE,
  REFERENCE_POINT,
  PARKING_LOT,
  CONTACT_POINT,
  TOILET,
  RELATION,

  LINE,
  SUBLINE,

  ;

  public static final Set<BusinessObjectType> SEPODI_BUSINESS_OBJECTS = Set.of(SERVICE_POINT, TRAFFIC_POINT, LOADING_POINT, SECTOR);
  public static final Set<BusinessObjectType> PRM_BUSINESS_OBJECTS = Set.of(STOP_POINT, PLATFORM_REDUCED, PLATFORM_COMPLETE,
      PLATFORM,
      REFERENCE_POINT, PARKING_LOT, CONTACT_POINT, TOILET, RELATION);
  public static final Set<BusinessObjectType> LIDI_BUSINESS_OBJECTS = Set.of(LINE, SUBLINE);

}
