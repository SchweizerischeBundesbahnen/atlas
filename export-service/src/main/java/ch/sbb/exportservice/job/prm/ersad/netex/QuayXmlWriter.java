package ch.sbb.exportservice.job.prm.ersad.netex;

import static ch.sbb.exportservice.job.prm.ersad.model.Constants.ACCESS_VEHICLE_EQUIPMENT;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.HOIST;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.ID_SUFFIX_ACCESS;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.ID_SUFFIX_ACCESSIBILITY_ASSESSMENT;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.LEVEL_ACCESS_INTO_VEHICLE;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.PLACE_EQUIPMENTS;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.PUBLIC_CODE;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.QUAY;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.RAMP;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.WHEELCHAIR_ACCESS;

import ch.sbb.exportservice.job.prm.ersad.model.ErsadQuay;
import ch.sbb.exportservice.job.prm.ersad.netex.XmlWriterSupport.Limitation;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class QuayXmlWriter {

  private final XmlWriterSupport xml;

  void write(ErsadQuay quay) {
    xml.startElement(QUAY, quay.sloid());
    xml.accessibilityAssessment(quay.sloid() + ID_SUFFIX_ACCESSIBILITY_ASSESSMENT,
        List.of(
            new Limitation(WHEELCHAIR_ACCESS, NetexValueMapper.limitation(quay.vehicleAccess())),
            new Limitation(LEVEL_ACCESS_INTO_VEHICLE, NetexValueMapper.limitation(quay.levelAccessWheelchair()))),
        NetexValueMapper.text(quay.adviceAccessInfo()));
    writeAccessVehicleEquipment(quay);
    xml.optionalElement(PUBLIC_CODE, NetexValueMapper.text(quay.designation()));
    xml.endElement();
  }

  private void writeAccessVehicleEquipment(ErsadQuay quay) {
    Optional<String> hoist = NetexValueMapper.hoist(quay.boardingDevice());
    Optional<String> ramp = NetexValueMapper.ramp(quay.boardingDevice());
    if (!XmlWriterSupport.anyPresent(List.of(hoist, ramp))) {
      return;
    }
    xml.startElement(PLACE_EQUIPMENTS);
    xml.startElement(ACCESS_VEHICLE_EQUIPMENT, quay.sloid() + ID_SUFFIX_ACCESS);
    xml.optionalElement(HOIST, hoist);
    xml.optionalElement(RAMP, ramp);
    xml.endElement();
    xml.endElement();
  }
}
