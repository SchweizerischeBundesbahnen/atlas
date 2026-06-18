package ch.sbb.exportservice.job.prm.contactpoint.processor;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_TIME_FORMATTER;

import ch.sbb.exportservice.job.prm.contactpoint.entity.ContactPointVersion;
import ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

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
        .validFrom(DATE_FORMATTER_BASE.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER_BASE.format(version.getValidTo()))
        .creationDate(LOCAL_DATE_TIME_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_TIME_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

}
