package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.api.prm.enumeration.RecordingStatus;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointOverviewModel;
import ch.sbb.atlas.api.prm.model.contactpoint.ContactPointVersionModel;
import ch.sbb.atlas.api.prm.model.contactpoint.ReadContactPointVersionModel;
import ch.sbb.atlas.location.SloidHelper;
import ch.sbb.prm.directory.entity.ContactPointVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ContactPointVersionMapper {

  public static ReadContactPointVersionModel toModel(ContactPointVersion version) {
    return ReadContactPointVersionModel.builder()
        .id(version.getId())
        .status(version.getStatus())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .designation(version.getDesignation())
        .additionalInformation(version.getAdditionalInformation())
        .inductionLoop(version.getInductionLoop())
        .openingHours(version.getOpeningHours())
        .wheelchairAccess(version.getWheelchairAccess())
        .type(version.getType())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

  public static ContactPointOverviewModel toOverviewModel(ContactPointVersion version, RecordingStatus recordingStatus) {
    return ContactPointOverviewModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .designation(version.getDesignation())
        .additionalInformation(version.getAdditionalInformation())
        .inductionLoop(version.getInductionLoop())
        .openingHours(version.getOpeningHours())
        .wheelchairAccess(version.getWheelchairAccess())
        .type(version.getType())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .recordingStatus(recordingStatus)
        .build();
  }

  public static ContactPointVersion toEntity(ContactPointVersionModel model) {
    ContactPointVersion entity = ContactPointVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .number(SloidHelper.getServicePointNumber(model.getParentServicePointSloid()))
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .designation(model.getDesignation())
        .additionalInformation(model.getAdditionalInformation())
        .inductionLoop(model.getInductionLoop())
        .openingHours(model.getOpeningHours())
        .wheelchairAccess(model.getWheelchairAccess())
        .type(model.getType())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .version(model.getEtagVersion())
        .build();
    if (entity.getWheelchairAccess() == null) {
      entity.setWheelchairAccess(StandardAttributeType.TO_BE_COMPLETED);
    }
    if (entity.getInductionLoop() == null) {
      entity.setInductionLoop(StandardAttributeType.TO_BE_COMPLETED);
    }
    return entity;
  }

}
