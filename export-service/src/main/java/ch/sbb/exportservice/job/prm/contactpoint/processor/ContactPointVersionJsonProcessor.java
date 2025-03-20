package ch.sbb.exportservice.job.prm.contactpoint.processor;

import ch.sbb.atlas.api.prm.model.contactpoint.ReadContactPointVersionModel;
import ch.sbb.exportservice.job.prm.contactpoint.entity.ContactPointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ContactPointVersionJsonProcessor implements ItemProcessor<ContactPointVersion,
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
        .status(version.getStatus())
        .build();
  }

}
