package ch.sbb.exportservice.job.prm.ersad.assembler;

import ch.sbb.atlas.api.servicepoint.SpatialReference;
import ch.sbb.atlas.servicepoint.CoordinatePair;
import ch.sbb.atlas.servicepoint.transformer.CoordinateTransformer;
import ch.sbb.exportservice.job.prm.contactpoint.entity.ContactPointVersion;
import ch.sbb.exportservice.job.prm.ersad.accessibility.ErsadAccessibilityDeriver;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadAddress;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadAssistance;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadContactPoint;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadParking;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadPathLink;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadQuay;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStation;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStationEquipment;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadToilet;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.relation.entity.RelationVersion;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import ch.sbb.exportservice.job.prm.toilet.entity.ToiletVersion;
import ch.sbb.exportservice.job.sepodi.servicepoint.entity.ServicePointVersion;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
      Map<String, List<ch.sbb.exportservice.job.prm.parkinglot.entity.ParkingLotVersion>> parkingLotsByParentSloid,
      LocalDate exportDate) {
    return stopPointsBySloid.values().stream()
        .map(stopPoint -> toErsadStation(stopPoint, servicePointsBySloid, platformsByParentSloid, trafficPointDesignations,
            contactPointsByParentSloid, toiletsByParentSloid, relationsByParentSloid, parkingLotsByParentSloid, exportDate))
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
      Map<String, List<ch.sbb.exportservice.job.prm.parkinglot.entity.ParkingLotVersion>> parkingLotsByParentSloid,
      LocalDate exportDate) {
    ServicePointVersion servicePoint = servicePointsBySloid.get(stopPoint.getSloid());
    if (servicePoint == null) {
      log.warn("Skipping station {} because service point is missing in SEPODI", stopPoint.getSloid());
      return Optional.empty();
    }

    List<PlatformVersion> platforms = platformsByParentSloid.getOrDefault(stopPoint.getSloid(), List.of());
    List<RelationVersion> relations = relationsByParentSloid.getOrDefault(stopPoint.getSloid(), List.of());

    CoordinatePair wgs84 = null;
    if (servicePoint.getServicePointGeolocation() != null
        && servicePoint.getServicePointGeolocation().getSpatialReference() != null) {
      wgs84 = coordinateTransformer.transform(servicePoint.getServicePointGeolocation().asCoordinatePair(), SpatialReference.WGS84);
    }

    return Optional.of(new ErsadStation(
        stopPoint.getSloid(),
        stopPoint.getNumber().getNumber(),
        Optional.ofNullable(servicePoint.getDesignationOfficial()).orElse(stopPoint.getFreeText()),
        wgs84 == null ? null : wgs84.getEast(),
        wgs84 == null ? null : wgs84.getNorth(),
        new ErsadAddress(stopPoint.getAddress(), stopPoint.getZipCode(), stopPoint.getCity()),
        stopPoint.getUrl(),
        stopPoint.getMeansOfTransport().stream().map(Enum::name).sorted().toList(),
        accessibilityDeriver.derive(stopPoint, platforms, relations, exportDate),
        new ErsadAssistance(
            stopPoint.getAssistanceAvailability(),
            stopPoint.getAssistanceCondition(),
            stopPoint.getAssistanceService() == null ? null : stopPoint.getAssistanceService().name(),
            stopPoint.getAssistanceRequestFulfilled(),
            stopPoint.getAlternativeTransport(),
            stopPoint.getAlternativeTransportCondition()),
        new ErsadStationEquipment(
            stopPoint.getVisualInfo(),
            stopPoint.getDynamicOpticSystem(),
            stopPoint.getDynamicAudioSystem(),
            stopPoint.getTicketMachine(),
            stopPoint.getWheelchairTicketMachine(),
            stopPoint.getAudioTicketMachine()),
        platforms.stream()
            .sorted(Comparator.comparing(PlatformVersion::getSloid))
            .map(platform -> new ErsadQuay(
                platform.getSloid(),
                trafficPointDesignations.get(platform.getSloid()),
                platform.getBoardingDevice(),
                platform.getAdviceAccessInfo(),
                platform.getContrastingAreas(),
                platform.getLevelAccessWheelchair(),
                platform.getSuperelevation(),
                platform.getShuttle(),
                platform.getVehicleAccess()))
            .toList(),
        contactPointsByParentSloid.getOrDefault(stopPoint.getSloid(), List.of()).stream()
            .sorted(Comparator.comparing(ContactPointVersion::getSloid))
            .map(contactPoint -> new ErsadContactPoint(
                contactPoint.getSloid(),
                contactPoint.getType(),
                contactPoint.getDesignation(),
                contactPoint.getOpeningHours(),
                contactPoint.getWheelchairAccess(),
                contactPoint.getInductionLoop()))
            .toList(),
        toiletsByParentSloid.getOrDefault(stopPoint.getSloid(), List.of()).stream()
            .sorted(Comparator.comparing(ToiletVersion::getSloid))
            .map(toilet -> new ErsadToilet(toilet.getSloid(), toilet.getDesignation(), toilet.getWheelchairToilet()))
            .toList(),
        parkingLotsByParentSloid.getOrDefault(stopPoint.getSloid(), List.of()).stream()
            .sorted(Comparator.comparing(ch.sbb.exportservice.job.prm.parkinglot.entity.ParkingLotVersion::getSloid))
            .map(parking -> new ErsadParking(parking.getSloid(), parking.getDesignation(), parking.getPlacesAvailable(),
                parking.getPrmPlacesAvailable()))
            .toList(),
        relations.stream()
            .sorted(Comparator.comparing(RelationVersion::getSloid))
            .map(relation -> new ErsadPathLink(
                relation.getReferencePointSloid(),
                relation.getSloid(),
                relation.getReferencePointElementType(),
                relation.getStepFreeAccess(),
                relation.getTactileVisualMarks(),
                relation.getContrastingAreas()))
            .toList()));
  }
}
