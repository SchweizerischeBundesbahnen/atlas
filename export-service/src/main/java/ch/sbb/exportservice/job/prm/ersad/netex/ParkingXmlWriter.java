package ch.sbb.exportservice.job.prm.ersad.netex;

import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ID_SUFFIX_PARKING_AREA;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ID_SUFFIX_PARKING_PROPERTIES;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.NAME;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PARENT_SITE_REF;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PARKING;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PARKING_AREA;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PARKING_AREAS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PARKING_PROPERTIES;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PARKING_PROPERTIES_CONTAINER;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PARKING_USER_TYPES;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PARKING_USER_TYPE_REGISTERED_DISABLED;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadParking;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ParkingXmlWriter {

  private final XmlWriterSupport xml;

  void write(ErsadStation station, ErsadParking parking) {
    xml.startElement(PARKING, parking.sloid());
    xml.optionalElement(NAME, NetexValueMapper.text(parking.designation()));
    xml.reference(PARENT_SITE_REF, station.sloid());
    if (parking.prmPlacesAvailable() == BooleanOptionalAttributeType.YES) {
      xml.startElement(PARKING_AREAS);
      xml.startElement(PARKING_AREA, parking.sloid() + ID_SUFFIX_PARKING_AREA);
      xml.startElement(PARKING_PROPERTIES_CONTAINER);
      xml.startElement(PARKING_PROPERTIES, parking.sloid() + ID_SUFFIX_PARKING_AREA + ID_SUFFIX_PARKING_PROPERTIES);
      xml.element(PARKING_USER_TYPES, PARKING_USER_TYPE_REGISTERED_DISABLED);
      xml.endElement();
      xml.endElement();
      xml.endElement();
      xml.endElement();
    }
    xml.endElement();
  }
}
