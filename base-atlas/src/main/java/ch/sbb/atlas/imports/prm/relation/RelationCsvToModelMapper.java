package ch.sbb.atlas.imports.prm.relation;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointElementType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StepFreeAccessAttributeType;
import ch.sbb.atlas.api.prm.enumeration.TactileVisualAttributeType;
import ch.sbb.atlas.api.prm.model.relation.RelationVersionModel;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelationCsvToModelMapper {

  private static final Set<String> CONTACT_POINT_TYPES = Set.of("TICKET_COUNTER", "INFO_DESK");

  public RelationVersionModel toModel(RelationCsvModel relationCsvModel) {
    return RelationVersionModel.builder()
        .parentServicePointSloid(relationCsvModel.getDsSloid())
        .elementSloid(relationCsvModel.getSloid())
        .referencePointSloid(relationCsvModel.getRpSloid())
        .referencePointElementType(getReferencePointElementType(relationCsvModel))
        .tactileVisualMarks(TactileVisualAttributeType.of(relationCsvModel.getTactVisualMarks()))
        .contrastingAreas(StandardAttributeType.from(relationCsvModel.getContrastingAreas()))
        .stepFreeAccess(StepFreeAccessAttributeType.of(relationCsvModel.getStepFreeAccess()))
        .validFrom(relationCsvModel.getValidFrom())
        .validTo(relationCsvModel.getValidTo())
        .creationDate(relationCsvModel.getCreatedAt())
        .creator(relationCsvModel.getAddedBy())
        .editionDate(relationCsvModel.getModifiedAt())
        .editor(relationCsvModel.getModifiedBy())
        .build();
  }

  private static ReferencePointElementType getReferencePointElementType(RelationCsvModel relationCsvModel) {
    String relationType = relationCsvModel.getElType().toUpperCase();
    if (CONTACT_POINT_TYPES.contains(relationType)) {
      return ReferencePointElementType.CONTACT_POINT;
    }
    return ReferencePointElementType.valueOf(relationType);
  }
}
