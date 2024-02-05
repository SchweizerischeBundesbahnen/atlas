package ch.sbb.exportservice.processor;

import ch.sbb.exportservice.entity.ContactPointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ContactPointVersionJsonProcessor extends BaseServicePointProcessor implements ItemProcessor<ContactPointVersion,
        ReadContactPointVersionModel> {

    @Override
    public ReadContactPointVersionModel process(ContactPointVersion version) {
        return ReadContactPointVersionModel.builder()
                .id(version.getId())
                .sloid(version.getSloid())
                .number(version.getParentServicePointNumber())
                .parentServicePointSloid(version.getParentServicePointSloid())
                .type(version.getType())
                .designation(version.getDesignation())
                .additionalInformation(version.getAdditionalInformation())
                .inductionLoop(version.getInductionLoop())
                .openingHours(version.getOpeningHours())
                .wheelchairAccess(version.getWheelchairAccess())
                .validFrom(version.getValidFrom())
                .validTo(version.getValidTo())
                .creationDate(version.getCreationDate())
                .creator(version.getCreator())
                .editionDate(version.getEditionDate())
                .editor(version.getEditor())
                .etagVersion(version.getVersion())
                .build();
    }

}
