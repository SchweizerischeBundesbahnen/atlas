package ch.sbb.exportservice.job.prm.ersad.netex;

import static ch.sbb.exportservice.job.prm.ersad.model.Constants.FROM;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.ID_SUFFIX_ACCESSIBILITY_ASSESSMENT;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.ID_SUFFIX_PATH_LINK;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.PLACE_REF;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.SITE_PATH_LINK;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.STEP_FREE_ACCESS;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.TACTILE_GUIDANCE_AVAILABLE;
import static ch.sbb.exportservice.job.prm.ersad.model.Constants.TO;

import ch.sbb.exportservice.job.prm.ersad.model.ErsadPathLink;
import ch.sbb.exportservice.job.prm.ersad.model.ErsadStation;
import ch.sbb.exportservice.job.prm.ersad.netex.XmlWriterSupport.Limitation;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class PathLinkXmlWriter {

  private final XmlWriterSupport xml;

  void write(ErsadStation station, ErsadPathLink pathLink, int index) {
    String id = station.sloid() + ID_SUFFIX_PATH_LINK + index;
    xml.startElement(SITE_PATH_LINK, id);
    xml.startElement(FROM);
    xml.referenceWithoutVersion(PLACE_REF, pathLink.referencePointSloid());
    xml.endElement();
    xml.startElement(TO);
    xml.referenceWithoutVersion(PLACE_REF, pathLink.targetSloid());
    xml.endElement();
    xml.accessibilityAssessment(id + ID_SUFFIX_ACCESSIBILITY_ASSESSMENT,
        List.of(
            new Limitation(STEP_FREE_ACCESS, NetexValueMapper.limitation(pathLink.stepFreeAccess())),
            new Limitation(TACTILE_GUIDANCE_AVAILABLE, NetexValueMapper.limitation(pathLink.tactileVisualMarks()))),
        Optional.empty());
    xml.endElement();
  }
}
