package ch.sbb.importservice.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(enumAsRef = true)
public enum BusinessObjectType {

  SERVICE_POINT,
  TRAFFIC_POINT,
  LOADING_POINT,

  STOP_POINT,
  PLATFORM_REDUCED,
  PLATFORM_COMPLETE,
  REFERENCE_POINT,
  PARKING_LOT,
  CONTACT_POINT,
  TOILET,
  RELATION,

  ;

  public static final Set<BusinessObjectType> SEPODI_BUSINESS_OBJECTS = Set.of(SERVICE_POINT, TRAFFIC_POINT, LOADING_POINT);
  public static final Set<BusinessObjectType> PRM_BUSINESS_OBJECTS = Set.of(STOP_POINT, PLATFORM_REDUCED, PLATFORM_COMPLETE,
      REFERENCE_POINT, PARKING_LOT, CONTACT_POINT, TOILET, RELATION);

}
