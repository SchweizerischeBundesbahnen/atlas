package ch.sbb.exportservice.job.prm.ersad.model;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;

public record ErsadPathLink(
    String referencePointSloid,
    String targetSloid,
    ReferencePointElementType targetType,
    StepFreeAccessAttributeType stepFreeAccess,
    TactileVisualAttributeType tactileVisualMarks,
    StandardAttributeType contrastingAreas) {

}
