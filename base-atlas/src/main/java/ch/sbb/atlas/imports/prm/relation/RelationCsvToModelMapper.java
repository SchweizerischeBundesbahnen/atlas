package ch.sbb.atlas.imports.prm.relation;

import ch.sbb.atlas.api.prm.enumeration.*;
import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
import ch.sbb.atlas.api.prm.model.relation.RelationVersionModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RelationCsvToModelMapper {

    public RelationVersionModel toModel(RelationCsvModel relationCsvModel) {
        return RelationVersionModel.builder()
                .parentServicePointSloid(relationCsvModel.getDsSloid())
                .sloid(relationCsvModel.getSloid())
                .referencePointSloid(relationCsvModel.getRpSloid())
                .referencePointElementType(ReferencePointElementType.valueOf(relationCsvModel.getElType()))
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

}
