package ch.sbb.exportservice.job.prm.contactpoint.processor;

import ch.sbb.exportservice.job.prm.contactpoint.entity.ContactPointVersion;
import ch.sbb.exportservice.job.prm.contactpoint.model.ContactPointVersionCsvModel;
import ch.sbb.exportservice.util.MapperUtil;
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
        .validFrom(MapperUtil.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(MapperUtil.DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

}
