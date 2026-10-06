package ch.sbb.exportservice.job.prm.ersad.netex;

import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ADDRESS_LINE_1;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ASSISTANCE_AVAILABILITY;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ASSISTANCE_FACILITY_LIST;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ASSISTANCE_SERVICE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ATTR_TYPE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.AUDIBLE_SIGNALS_AVAILABLE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.AUDIO_INTERFACE_AVAILABLE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.BOARDING_ASSISTANCE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.CENTROID;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.DIDOK;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ID_SUFFIX_ACCESSIBILITY_ASSESSMENT;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ID_SUFFIX_ADDRESS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ID_SUFFIX_ASSISTANCE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.INDUCTION_LOOPS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.LATITUDE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.LOCAL_SERVICES;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.LOCATION;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.LONGITUDE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.NAME;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.NOTE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PLACE_EQUIPMENTS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.POSTAL_ADDRESS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.POST_CODE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PRIVATE_CODE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.QUAYS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.RAIL_STATION;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.SANITARY_EQUIPMENT;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.SANITARY_FACILITY_LIST;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.STOP_PLACE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.STOP_PLACE_TYPE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TICKETING_EQUIPMENT;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TICKETING_SERVICE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TICKET_MACHINES;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TICKET_OFFICE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TOWN;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.TRANSPORT_MODE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.URL;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.WHEELCHAIR_ACCESS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.WHEELCHAIR_SUITABLE;

import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadAddress;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadAssistance;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadContactPoint;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStation;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStationEquipment;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadToilet;
import ch.sbb.exportservice.job.prm.ersad.netex.XmlWriterSupport.Limitation;
import java.util.List;
import java.util.Optional;

class StopPlaceXmlWriter {

  private final XmlWriterSupport xml;
  private final QuayXmlWriter quayWriter;

  StopPlaceXmlWriter(XmlWriterSupport xml) {
    this.xml = xml;
    this.quayWriter = new QuayXmlWriter(xml);
  }

  void write(ErsadStation station) {
    xml.startElement(STOP_PLACE, station.sloid());
    xml.optionalElement(NAME, NetexValueMapper.text(station.name()));
    if (station.number() != null) {
      xml.element(PRIVATE_CODE, ATTR_TYPE, DIDOK, String.valueOf(station.number()));
    }
    writeCentroid(station);
    xml.optionalElement(URL, NetexValueMapper.text(station.url()));
    writePostalAddress(station);
    writeAccessibilityAssessment(station);
    writePlaceEquipments(station);
    writeLocalServices(station);
    xml.optionalElement(TRANSPORT_MODE, transportMode(station));
    if (station.isTrainStation()) {
      xml.element(STOP_PLACE_TYPE, RAIL_STATION);
    }
    if (!station.quays().isEmpty()) {
      xml.startElement(QUAYS);
      station.quays().forEach(quayWriter::write);
      xml.endElement();
    }
    xml.endElement();
  }

  private void writeCentroid(ErsadStation station) {
    Optional<String> longitude = NetexValueMapper.coordinate(station.longitude());
    Optional<String> latitude = NetexValueMapper.coordinate(station.latitude());
    if (longitude.isEmpty() || latitude.isEmpty()) {
      return;
    }
    xml.startElement(CENTROID);
    xml.startElement(LOCATION);
    xml.optionalElement(LONGITUDE, longitude);
    xml.optionalElement(LATITUDE, latitude);
    xml.endElement();
    xml.endElement();
  }

  private void writePostalAddress(ErsadStation station) {
    ErsadAddress address = station.address();
    if (address == null) {
      return;
    }
    Optional<String> street = NetexValueMapper.text(address.street());
    Optional<String> zipCode = NetexValueMapper.text(address.zipCode());
    Optional<String> city = NetexValueMapper.text(address.city());
    if (!XmlWriterSupport.anyPresent(List.of(street, zipCode, city))) {
      return;
    }
    xml.startElement(POSTAL_ADDRESS, station.sloid() + ID_SUFFIX_ADDRESS);
    xml.optionalElement(ADDRESS_LINE_1, street);
    xml.optionalElement(TOWN, city);
    xml.optionalElement(POST_CODE, zipCode);
    xml.endElement();
  }

  private void writeAccessibilityAssessment(ErsadStation station) {
    ErsadStationEquipment equipment = station.equipment();
    Optional<String> accessible = NetexValueMapper.triState(station.accessibleStation());
    xml.accessibilityAssessment(station.sloid() + ID_SUFFIX_ACCESSIBILITY_ASSESSMENT,
        List.of(
            new Limitation(WHEELCHAIR_ACCESS, accessible),
            new Limitation(AUDIBLE_SIGNALS_AVAILABLE,
                equipment == null ? Optional.empty() : NetexValueMapper.limitation(equipment.dynamicAudioSystem()))),
        Optional.empty());
  }

  private void writeLocalServices(ErsadStation station) {
    ErsadAssistance assistance = station.assistance();
    Optional<String> note = assistance == null ? Optional.empty() : NetexValueMapper.text(assistance.assistanceCondition());
    Optional<String> facilityList = assistance != null && assistance.isAssistanceOffered()
        ? Optional.of(BOARDING_ASSISTANCE) : Optional.empty();
    Optional<String> availability = assistance == null
        ? Optional.empty() : NetexValueMapper.assistanceAvailability(assistance.assistanceAvailability());
    boolean hasAssistanceService = XmlWriterSupport.anyPresent(List.of(note, facilityList, availability));
    List<ErsadContactPoint> informationDesks = station.informationDesks();
    if (!hasAssistanceService && informationDesks.isEmpty()) {
      return;
    }
    xml.startElement(LOCAL_SERVICES);
    if (hasAssistanceService) {
      xml.startElement(ASSISTANCE_SERVICE, station.sloid() + ID_SUFFIX_ASSISTANCE);
      xml.optionalElement(NOTE, note);
      xml.optionalElement(ASSISTANCE_FACILITY_LIST, facilityList);
      xml.optionalElement(ASSISTANCE_AVAILABILITY, availability);
      xml.endElement();
    }
    informationDesks.forEach(this::writeTicketingService);
    xml.endElement();
  }

  private void writeTicketingService(ErsadContactPoint contactPoint) {
    xml.startElement(TICKETING_SERVICE, contactPoint.sloid());
    xml.optionalElement(NAME, NetexValueMapper.text(contactPoint.designation()));
    xml.endElement();
  }

  private void writePlaceEquipments(ErsadStation station) {
    List<ErsadContactPoint> ticketCounters = station.ticketCounters();
    if (ticketCounters.isEmpty() && station.toilets().isEmpty()) {
      return;
    }
    xml.startElement(PLACE_EQUIPMENTS);
    ticketCounters.forEach(contactPoint -> writeTicketingEquipment(station, contactPoint));
    station.toilets().forEach(this::writeSanitaryEquipment);
    xml.endElement();
  }

  private void writeTicketingEquipment(ErsadStation station, ErsadContactPoint contactPoint) {
    ErsadStationEquipment equipment = station.equipment();
    xml.startElement(TICKETING_EQUIPMENT, contactPoint.sloid());
    xml.optionalElement(TICKET_MACHINES,
        equipment == null ? Optional.empty() : NetexValueMapper.limitation(equipment.ticketMachine()));
    xml.optionalElement(TICKET_OFFICE, NetexValueMapper.ticketOffice(contactPoint.type()));
    xml.optionalElement(INDUCTION_LOOPS, NetexValueMapper.booleanFlag(contactPoint.inductionLoop()));
    xml.optionalElement(AUDIO_INTERFACE_AVAILABLE,
        equipment == null ? Optional.empty() : NetexValueMapper.booleanFlag(equipment.audioTicketMachine()));
    xml.optionalElement(WHEELCHAIR_SUITABLE, NetexValueMapper.booleanFlag(contactPoint.wheelchairAccess()));
    xml.endElement();
  }

  private void writeSanitaryEquipment(ErsadToilet toilet) {
    xml.startElement(SANITARY_EQUIPMENT, toilet.sloid());
    xml.optionalElement(NAME, NetexValueMapper.text(toilet.designation()));
    xml.accessibilityAssessment(toilet.sloid() + ID_SUFFIX_ACCESSIBILITY_ASSESSMENT,
        List.of(new Limitation(WHEELCHAIR_ACCESS, NetexValueMapper.limitation(toilet.wheelchairToilet()))),
        Optional.empty());
    xml.optionalElement(SANITARY_FACILITY_LIST, NetexValueMapper.sanitaryFacilityList(toilet.wheelchairToilet()));
    xml.endElement();
  }

  private static Optional<String> transportMode(ErsadStation station) {
    if (station.isTrainStation()) {
      return NetexValueMapper.transportMode(MeanOfTransport.TRAIN.name());
    }
    return station.meansOfTransport().stream()
        .map(NetexValueMapper::transportMode)
        .flatMap(Optional::stream)
        .findFirst();
  }
}
