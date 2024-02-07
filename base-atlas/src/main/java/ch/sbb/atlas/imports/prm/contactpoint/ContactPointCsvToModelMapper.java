package ch.sbb.atlas.imports.prm.contactpoint;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointVersionModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ContactPointCsvToModelMapper {

    public ContactPointVersionModel toModel(ContactPointCsvModel contactPointCsvModel) {
        return ContactPointVersionModel.builder()
                .parentServicePointSloid(contactPointCsvModel.getDsSloid())
                .sloid(contactPointCsvModel.getSloid())
                .designation(contactPointCsvModel.getDescription())
                .additionalInformation(contactPointCsvModel.getInfos())
                .inductionLoop(mapStandardAttributeType(contactPointCsvModel.getInductionLoop()))
                .openingHours(contactPointCsvModel.getOpenHours())
                .wheelchairAccess(mapStandardAttributeType(contactPointCsvModel.getWheelChairAccess()))
                .validFrom(contactPointCsvModel.getValidFrom())
                .validTo(contactPointCsvModel.getValidTo())
                .creationDate(contactPointCsvModel.getCreatedAt())
                .creator(contactPointCsvModel.getAddedBy())
                .editionDate(contactPointCsvModel.getModifiedAt())
                .editor(contactPointCsvModel.getModifiedBy())
                .build();
    }

    StandardAttributeType mapStandardAttributeType(Integer standardAttributeTypeCode) {
        return standardAttributeTypeCode != null ? StandardAttributeType.from(standardAttributeTypeCode) : null;
    }
}
