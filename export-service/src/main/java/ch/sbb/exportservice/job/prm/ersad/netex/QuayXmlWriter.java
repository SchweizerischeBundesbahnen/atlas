package ch.sbb.exportservice.job.prm.ersad.netex;

import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ACCESS_VEHICLE_EQUIPMENT;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.HOIST;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ID_SUFFIX_ACCESS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.ID_SUFFIX_ACCESSIBILITY_ASSESSMENT;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.LEVEL_ACCESS_INTO_VEHICLE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PLACE_EQUIPMENTS;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.PUBLIC_CODE;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.QUAY;
import static ch.sbb.exportservice.job.prm.ersad.model.NetexConstants.RAMP;

import ch.sbb.exportservice.job.prm.ersad.model.ErsadQuay;
import ch.sbb.exportservice.job.prm.ersad.netex.XmlWriterSupport.Limitation;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QuayXmlWriter {

  private final XmlWriterSupport xml;

  public void write(ErsadQuay quay) {
    xml.startElement(QUAY, quay.sloid(), quay.version());
    xml.accessibilityAssessment(quay.sloid() + ID_SUFFIX_ACCESSIBILITY_ASSESSMENT, quay.version(),
        List.of(
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
    xml.startElement(ACCESS_VEHICLE_EQUIPMENT, quay.sloid() + ID_SUFFIX_ACCESS, quay.version());
    xml.optionalElement(HOIST, hoist);
    xml.optionalElement(RAMP, ramp);
    xml.endElement();
    xml.endElement();
  }
}
