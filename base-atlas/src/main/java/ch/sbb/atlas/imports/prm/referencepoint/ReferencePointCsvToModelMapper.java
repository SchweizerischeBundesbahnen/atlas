package ch.sbb.atlas.imports.prm.referencepoint;

import ch.sbb.atlas.api.prm.enumeration.ReferencePointAttributeType;
import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReferencePointCsvToModelMapper {

    public ReferencePointVersionModel toModel(ReferencePointCsvModel referencePointCsvModel) {
        return ReferencePointVersionModel.builder()
                .parentServicePointSloid(referencePointCsvModel.getDsSloid())
                .sloid(referencePointCsvModel.getSloid())
                .designation(referencePointCsvModel.getDescription())
                .mainReferencePoint(referencePointCsvModel.getExport() == 1)
                .additionalInformation(referencePointCsvModel.getInfos())
                .referencePointType(ReferencePointAttributeType.of(referencePointCsvModel.getRpType()))
                .validFrom(referencePointCsvModel.getValidFrom())
                .validTo(referencePointCsvModel.getValidTo())
                .creationDate(referencePointCsvModel.getCreatedAt())
                .creator(referencePointCsvModel.getAddedBy())
                .editionDate(referencePointCsvModel.getModifiedAt())
                .editor(referencePointCsvModel.getModifiedBy())
                .build();
    }

}
