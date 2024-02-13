package ch.sbb.exportservice.processor;

import ch.sbb.atlas.export.model.prm.ContactPointVersionCsvModel;
import ch.sbb.exportservice.entity.ContactPointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ContactPointVersionCsvProcessor implements
        ItemProcessor<ContactPointVersion, ContactPointVersionCsvModel> {

    @Override
    public ContactPointVersionCsvModel process(ContactPointVersion version) {
        return ContactPointVersionCsvModel.builder()
                .sloid(version.getSloid())
                .parentSloidServicePoint(version.getParentServicePointSloid())
                .parentNumberServicePoint(version.getParentServicePointNumber().getNumber())
                .type(version.getType().toString())
                .designation(version.getDesignation())
                .additionalInformation(version.getAdditionalInformation())
                .inductionLoop(version.getInductionLoop().toString())
                .openingHours(version.getOpeningHours())
                .wheelchairAccess(version.getWheelchairAccess().toString())
                .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidFrom()))
                .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidTo()))
                .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
                .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
                .build();
    }

}
