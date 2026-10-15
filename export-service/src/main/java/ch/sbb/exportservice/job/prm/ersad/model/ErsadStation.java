package ch.sbb.exportservice.job.prm.ersad.model;

import ch.sbb.atlas.api.prm.enumeration.ContactPointType;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.util.List;
import java.util.Objects;

public record ErsadStation(
    String sloid,
    Integer number,
    String name,
    Double longitude,
    Double latitude,
    ErsadAddress address,
    String url,
    List<String> meansOfTransport,
    NetexTriState accessibleStation,
    ErsadAssistance assistance,
    ErsadStationEquipment equipment,
    List<ErsadQuay> quays,
    List<ErsadContactPoint> contactPoints,
    List<ErsadToilet> toilets,
    List<ErsadParking> parkings,
    List<ErsadPathLink> pathLinks,
    String version,
    List<ErsadReferencePoint> referencePoints) {

  public ErsadStation(String sloid, Integer number, String name, Double longitude, Double latitude, ErsadAddress address,
      String url, List<String> meansOfTransport, NetexTriState accessibleStation, ErsadAssistance assistance,
      ErsadStationEquipment equipment, List<ErsadQuay> quays, List<ErsadContactPoint> contactPoints,
      List<ErsadToilet> toilets, List<ErsadParking> parkings, List<ErsadPathLink> pathLinks, String version) {
    this(sloid, number, name, longitude, latitude, address, url, meansOfTransport, accessibleStation, assistance,
        equipment, quays, contactPoints, toilets, parkings, pathLinks, version, List.of());
  }

  public ErsadStation {
    meansOfTransport = meansOfTransport == null ? List.of() : List.copyOf(meansOfTransport);
    quays = quays == null ? List.of() : List.copyOf(quays);
    contactPoints = contactPoints == null ? List.of() : List.copyOf(contactPoints);
    toilets = toilets == null ? List.of() : List.copyOf(toilets);
    parkings = parkings == null ? List.of() : List.copyOf(parkings);
    pathLinks = pathLinks == null ? List.of() : List.copyOf(pathLinks);
    referencePoints = referencePoints == null ? List.of() : List.copyOf(referencePoints);
  }

  public boolean isTrainStation() {
    return meansOfTransport.stream()
        .filter(Objects::nonNull)
        .map(String::trim)
        .anyMatch(MeanOfTransport.TRAIN.name()::equalsIgnoreCase);
  }

  public List<ErsadContactPoint> ticketCounters() {
    return contactPointsOfType(ContactPointType.TICKET_COUNTER);
  }

  public List<ErsadContactPoint> informationDesks() {
    return contactPointsOfType(ContactPointType.INFORMATION_DESK);
  }

  private List<ErsadContactPoint> contactPointsOfType(ContactPointType type) {
    return contactPoints.stream()
        .filter(contactPoint -> contactPoint.type() == type)
        .toList();
  }
}
