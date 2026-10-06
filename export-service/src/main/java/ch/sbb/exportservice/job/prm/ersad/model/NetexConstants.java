package ch.sbb.exportservice.job.prm.ersad.model;

public final class NetexConstants {

  private NetexConstants() {
  }

  public static final String STOP_PLACE = "StopPlace";
  public static final String NAME = "Name";
  public static final String PRIVATE_CODE = "PrivateCode";
  public static final String CENTROID = "Centroid";
  public static final String LOCATION = "Location";
  public static final String LONGITUDE = "Longitude";
  public static final String LATITUDE = "Latitude";
  public static final String URL = "Url";
  public static final String POSTAL_ADDRESS = "PostalAddress";
  public static final String ADDRESS_LINE_1 = "AddressLine1";
  public static final String TOWN = "Town";
  public static final String POST_CODE = "PostCode";
  public static final String ACCESSIBILITY_ASSESSMENT = "AccessibilityAssessment";
  public static final String ACCESSIBILITY_LIMITATION = "AccessibilityLimitation";
  public static final String MOBILITY_IMPAIRED_ACCESS = "MobilityImpairedAccess";
  public static final String TRANSPORT_MODE = "TransportMode";
  public static final String STOP_PLACE_TYPE = "StopPlaceType";
  public static final String TICKETING_EQUIPMENT = "TicketingEquipment";
  public static final String SANITARY_EQUIPMENT = "SanitaryEquipment";
  public static final String TICKET_MACHINES = "TicketMachines";
  public static final String TICKET_OFFICE = "TicketOffice";
  public static final String INDUCTION_LOOPS = "InductionLoops";
  public static final String AUDIO_INTERFACE_AVAILABLE = "AudioInterfaceAvailable";
  public static final String WHEELCHAIR_SUITABLE = "WheelchairSuitable";
  public static final String AUDIBLE_SIGNALS_AVAILABLE = "AudibleSignalsAvailable";
  public static final String LOCAL_SERVICES = "localServices";
  public static final String PLACE_EQUIPMENTS = "placeEquipments";
  public static final String QUAYS = "quays";
  public static final String ASSISTANCE_SERVICE = "AssistanceService";
  public static final String ASSISTANCE_FACILITY_LIST = "AssistanceFacilityList";
  public static final String ASSISTANCE_AVAILABILITY = "AssistanceAvailability";
  public static final String TICKETING_SERVICE = "TicketingService";
  public static final String NOTE = "Note";
  public static final String SANITARY_FACILITY_LIST = "SanitaryFacilityList";
  public static final String QUAY = "Quay";
  public static final String PUBLIC_CODE = "PublicCode";
  public static final String ACCESS_VEHICLE_EQUIPMENT = "AccessVehicleEquipment";
  public static final String RAMP = "Ramp";
  public static final String HOIST = "Hoist";
  public static final String SITE_PATH_LINK = "SitePathLink";
  public static final String FROM = "From";
  public static final String TO = "To";
  public static final String PLACE_REF = "PlaceRef";
  public static final String STEP_FREE_ACCESS = "StepFreeAccess";
  public static final String TACTILE_GUIDANCE_AVAILABLE = "TactileGuidanceAvailable";
  public static final String PARKING = "Parking";
  public static final String PARKING_AREA = "ParkingArea";
  public static final String PARKING_PROPERTIES = "ParkingProperties";
  public static final String PARKING_USER_TYPES = "ParkingUserTypes";
  public static final String PARKING_AREAS = "parkingAreas";
  public static final String PARKING_PROPERTIES_CONTAINER = "parkingProperties";
  public static final String PARENT_SITE_REF = "ParentSiteRef";
  public static final String COMPOSITE_FRAME = "CompositeFrame";
  public static final String SITE_FRAME = "SiteFrame";
  public static final String PUBLICATION_DELIVERY = "PublicationDelivery";
  public static final String PUBLICATION_TIMESTAMP = "PublicationTimestamp";
  public static final String DESCRIPTION = "Description";
  public static final String DATA_OBJECTS = "dataObjects";
  public static final String FRAMES = "frames";
  public static final String STOP_PLACES = "stopPlaces";
  public static final String PATH_LINKS = "pathLinks";
  public static final String PARKINGS = "parkings";
  public static final String PARTICIPANT_REF = "ParticipantRef";
  public static final String LIMITATIONS = "limitations";
  public static final String COMMENT = "Comment";
  public static final String WHEELCHAIR_ACCESS = "WheelchairAccess";
  public static final String LEVEL_ACCESS_INTO_VEHICLE = "LevelAccessIntoVehicle";

  // XML attribute names
  public static final String ATTR_ID = "id";
  public static final String ATTR_VERSION = "version";
  public static final String ATTR_REF = "ref";
  public static final String ATTR_TYPE = "type";

  // NeTEx value literals
  public static final String TRUE = "true";
  public static final String FALSE = "false";
  public static final String PARTIAL = "partial";
  public static final String UNKNOWN = "unknown";
  public static final String NONE = "none";
  public static final String AVAILABLE_AT_CERTAIN_TIMES = "availableAtCertainTimes";
  public static final String AVAILABLE_IF_BOOKED = "availableIfBooked";
  public static final String BOARDING_ASSISTANCE = "boardingAssistance";
  public static final String RAIL_STATION = "railStation";
  public static final String DIDOK = "didok";
  public static final String PARKING_USER_TYPE_REGISTERED_DISABLED = "registeredDisabled";
  public static final String SANITARY_FACILITY_TOILET = "toilet";
  public static final String SANITARY_FACILITY_TOILET_WHEELCHAIR_ACCESSIBLE = "toilet wheelchairAccessToilet";
  public static final String PUBLICATION_DESCRIPTION = "ERSAD Accessibility Export - Vollexport / Snapshot";

  // Transport modes
  public static final String TRANSPORT_MODE_BUS = "bus";
  public static final String TRANSPORT_MODE_CABLEWAY = "cableway";
  public static final String TRANSPORT_MODE_FUNICULAR = "funicular";
  public static final String TRANSPORT_MODE_LIFT = "lift";
  public static final String TRANSPORT_MODE_METRO = "metro";
  public static final String TRANSPORT_MODE_RAIL = "rail";
  public static final String TRANSPORT_MODE_TRAM = "tram";
  public static final String TRANSPORT_MODE_WATER = "water";

  // Namespaces
  public static final String NETEX_NAMESPACE = "http://www.netex.org.uk/netex";
  public static final String GML_NAMESPACE = "http://www.opengis.net/gml/3.2";
  public static final String SIRI_NAMESPACE = "http://www.siri.org.uk/siri";
  public static final String GML_NAMESPACE_PREFIX = "gml";
  public static final String SIRI_NAMESPACE_PREFIX = "siri";

  // Formats & time zone
  public static final String ZURICH_ZONE_ID = "Europe/Zurich";
  public static final String TIMESTAMP_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX";
  public static final String FRAME_DATE_FORMAT_PATTERN = "yyyyMMdd";
  public static final String XML_ENCODING = "UTF-8";
  public static final String XML_VERSION = "1.0";

  // IDs & ID suffixes
  public static final String ATLAS_PARTICIPANT_REF = "ch:1:atlas";
  public static final String COMPOSITE_FRAME_ID_PREFIX = "ch:1:CompositeFrame:ERSAD:";
  public static final String SITE_FRAME_ID_PREFIX = "ch:1:SiteFrame:ERSAD:";
  public static final String ID_SUFFIX_ADDRESS = ":address";
  public static final String ID_SUFFIX_ACCESSIBILITY_ASSESSMENT = ":aa";
  public static final String ID_SUFFIX_ASSISTANCE = ":assistance";
  public static final String ID_SUFFIX_ACCESS = ":access";
  public static final String ID_SUFFIX_PARKING_AREA = ":parkingArea";
  public static final String ID_SUFFIX_PARKING_PROPERTIES = ":parkingProperties";
  public static final String ID_SUFFIX_PATH_LINK = ":pathLink:";

  // Numeric
  public static final String NETEX_OBJECT_VERSION = "1";
  public static final int COORDINATE_SCALE = 11;
}
