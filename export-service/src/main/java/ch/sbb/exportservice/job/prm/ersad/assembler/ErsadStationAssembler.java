package ch.sbb.exportservice.job.prm.ersad.assembler;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.exportservice.job.BaseEntity;
import ch.sbb.exportservice.job.prm.contactpoint.entity.ContactPointVersion;
import ch.sbb.exportservice.job.prm.ersad.accessibility.ErsadAccessibilityDeriver;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadAddress;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadAssistance;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadContactPoint;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadParking;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadPathLink;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadQuay;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadReferencePoint;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStation;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStationEquipment;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadToilet;
import ch.sbb.exportservice.job.prm.parkinglot.entity.ParkingLotVersion;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.referencepoint.entity.ReferencePointVersion;
import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import ch.sbb.exportservice.job.prm.toilet.entity.ToiletVersion;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ErsadStationAssembler {

  private final CoordinateTransformer coordinateTransformer = new CoordinateTransformer();
  private final ErsadAccessibilityDeriver accessibilityDeriver;

  public List<ErsadStation> assembleStations(
      Map<String, StopPointVersion> stopPointsBySloid,
      Map<String, ServicePointVersion> servicePointsBySloid,
      Map<String, List<PlatformVersion>> platformsByParentSloid,
      Map<String, String> trafficPointDesignations,
      Map<String, List<ContactPointVersion>> contactPointsByParentSloid,
      Map<String, List<ToiletVersion>> toiletsByParentSloid,
      Map<String, List<RelationVersion>> relationsByParentSloid,
      Map<String, List<ParkingLotVersion>> parkingLotsByParentSloid,
      LocalDate exportDate) {
    return assembleStations(stopPointsBySloid, servicePointsBySloid, platformsByParentSloid, trafficPointDesignations,
        contactPointsByParentSloid, toiletsByParentSloid, relationsByParentSloid, parkingLotsByParentSloid, Map.of(),
        exportDate);
  }

  public List<ErsadStation> assembleStations(
      Map<String, StopPointVersion> stopPointsBySloid,
      Map<String, ServicePointVersion> servicePointsBySloid,
      Map<String, List<PlatformVersion>> platformsByParentSloid,
      Map<String, String> trafficPointDesignations,
      Map<String, List<ContactPointVersion>> contactPointsByParentSloid,
      Map<String, List<ToiletVersion>> toiletsByParentSloid,
      Map<String, List<RelationVersion>> relationsByParentSloid,
      Map<String, List<ParkingLotVersion>> parkingLotsByParentSloid,
      Map<String, List<ReferencePointVersion>> referencePointsByParentSloid,
      LocalDate exportDate) {
    return stopPointsBySloid.values().stream()
        .map(stopPoint -> toErsadStation(
            stopPoint,
            servicePointsBySloid,
            platformsByParentSloid,
            trafficPointDesignations,
            contactPointsByParentSloid,
            toiletsByParentSloid,
            relationsByParentSloid,
            parkingLotsByParentSloid,
            referencePointsByParentSloid,
            exportDate
            )
        )
        .flatMap(Optional::stream)
        .toList();
  }

  private Optional<ErsadStation> toErsadStation(
      StopPointVersion stopPoint,
      Map<String, ServicePointVersion> servicePointsBySloid,
      Map<String, List<PlatformVersion>> platformsByParentSloid,
      Map<String, String> trafficPointDesignations,
      Map<String, List<ContactPointVersion>> contactPointsByParentSloid,
      Map<String, List<ToiletVersion>> toiletsByParentSloid,
      Map<String, List<RelationVersion>> relationsByParentSloid,
      Map<String, List<ParkingLotVersion>> parkingLotsByParentSloid,
      Map<String, List<ReferencePointVersion>> referencePointsByParentSloid,
      LocalDate exportDate) {
    ServicePointVersion servicePoint = servicePointsBySloid.get(stopPoint.getSloid());
    if (servicePoint == null) {
      log.warn("Skipping station {} because service point is missing in SEPODI", stopPoint.getSloid());
      return Optional.empty();
    }

    String sloid = stopPoint.getSloid();
    List<PlatformVersion> platforms = platformsByParentSloid.getOrDefault(sloid, List.of());
    List<RelationVersion> relations = relationsByParentSloid.getOrDefault(sloid, List.of());
    CoordinatePair wgs84 = toWgs84(servicePoint);

    List<ErsadContactPoint> contactPoints = mapSorted(contactPointsByParentSloid.getOrDefault(sloid, List.of()),
        ContactPointVersion::getSloid,
        ErsadStationAssembler::toContactPoint);
    return Optional.of(new ErsadStation(
        sloid,
        stopPoint.getNumber().getNumber(),
        Optional.ofNullable(servicePoint.getDesignationOfficial()).orElse(stopPoint.getFreeText()),
        wgs84 == null ? null : wgs84.getEast(),
        wgs84 == null ? null : wgs84.getNorth(),
        new ErsadAddress(stopPoint.getAddress(), stopPoint.getZipCode(), stopPoint.getCity()),
        stopPoint.getUrl(),
        stopPoint.getMeansOfTransport().stream().map(Enum::name).sorted().toList(),
        accessibilityDeriver.derive(stopPoint, platforms, relations, exportDate),
        toAssistance(stopPoint),
        toEquipment(stopPoint),
        mapSorted(platforms, PlatformVersion::getSloid,
            platform -> toQuay(platform, trafficPointDesignations.get(platform.getSloid()))),
        contactPoints,
        mapSorted(toiletsByParentSloid.getOrDefault(sloid, List.of()), ToiletVersion::getSloid,
            ErsadStationAssembler::toToilet),
        mapSorted(parkingLotsByParentSloid.getOrDefault(sloid, List.of()), ParkingLotVersion::getSloid,
            ErsadStationAssembler::toParking),
        mapSorted(relations, RelationVersion::getSloid, ErsadStationAssembler::toPathLink),
        netexVersion(stopPoint),
        mapSorted(referencePointsByParentSloid.getOrDefault(sloid, List.of()), ReferencePointVersion::getSloid,
            ErsadStationAssembler::toReferencePoint)));
  }

  private CoordinatePair toWgs84(ServicePointVersion servicePoint) {
    if (servicePoint.getServicePointGeolocation() == null
        || servicePoint.getServicePointGeolocation().getSpatialReference() == null) {
      return null;
    }
    return coordinateTransformer.transform(servicePoint.getServicePointGeolocation().asCoordinatePair(), SpatialReference.WGS84);
  }

  private static <T, R> List<R> mapSorted(List<T> items, Function<T, String> sortKey, Function<T, R> mapper) {
    return items.stream().sorted(Comparator.comparing(sortKey)).map(mapper).toList();
  }

  private static ErsadAssistance toAssistance(StopPointVersion stopPoint) {
    return new ErsadAssistance(
        stopPoint.getAssistanceAvailability(),
        stopPoint.getAssistanceCondition(),
        stopPoint.getAssistanceService() == null ? null : stopPoint.getAssistanceService().name(),
        stopPoint.getAssistanceRequestFulfilled(),
        stopPoint.getAlternativeTransport(),
        stopPoint.getAlternativeTransportCondition());
  }

  private static ErsadStationEquipment toEquipment(StopPointVersion stopPoint) {
    return new ErsadStationEquipment(
        stopPoint.getVisualInfo(),
        stopPoint.getDynamicOpticSystem(),
        stopPoint.getDynamicAudioSystem(),
        stopPoint.getTicketMachine(),
        stopPoint.getWheelchairTicketMachine(),
        stopPoint.getAudioTicketMachine());
  }

  private static ErsadQuay toQuay(PlatformVersion platform, String designation) {
    return new ErsadQuay(
        platform.getSloid(),
        designation,
        platform.getBoardingDevice(),
        platform.getAdviceAccessInfo(),
        platform.getContrastingAreas(),
        platform.getLevelAccessWheelchair(),
        platform.getSuperelevation(),
        platform.getShuttle(),
        netexVersion(platform));
  }

  private static ErsadContactPoint toContactPoint(ContactPointVersion contactPoint) {
    return new ErsadContactPoint(
        contactPoint.getSloid(),
        contactPoint.getType(),
        contactPoint.getDesignation(),
        contactPoint.getOpeningHours(),
        contactPoint.getWheelchairAccess(),
        contactPoint.getInductionLoop(),
        netexVersion(contactPoint));
  }

  private static ErsadToilet toToilet(ToiletVersion toilet) {
    return new ErsadToilet(toilet.getSloid(), toilet.getDesignation(), toilet.getWheelchairToilet(), netexVersion(toilet));
  }

  private static ErsadParking toParking(ParkingLotVersion parking) {
    return new ErsadParking(parking.getSloid(), parking.getDesignation(), parking.getPlacesAvailable(),
        parking.getPrmPlacesAvailable(), netexVersion(parking));
  }

  private static ErsadReferencePoint toReferencePoint(ReferencePointVersion referencePoint) {
    return new ErsadReferencePoint(
        referencePoint.getSloid(),
        referencePoint.getDesignation(),
        netexVersion(referencePoint));
  }

  private static ErsadPathLink toPathLink(RelationVersion relation) {
    return new ErsadPathLink(
        relation.getReferencePointSloid(),
        relation.getSloid(),
        relation.getReferencePointElementType(),
        relation.getStepFreeAccess(),
        relation.getTactileVisualMarks(),
        relation.getContrastingAreas(),
        netexVersion(relation));
  }

  /** The NeTEx version is the modification date, which strictly increases with every change of the entity. */
  private static String netexVersion(BaseEntity entity) {
    return entity.getEditionDate().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
  }
}
