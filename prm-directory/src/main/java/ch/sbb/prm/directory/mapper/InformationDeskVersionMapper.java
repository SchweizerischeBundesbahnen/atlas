package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.api.prm.model.informationdesk.InformationDeskVersionModel;
import ch.sbb.atlas.api.prm.model.informationdesk.ReadInformationDeskVersionModel;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InformationDeskVersionMapper {

  public static ReadInformationDeskVersionModel toModel(InformationDeskVersion version){
    return ReadInformationDeskVersionModel.builder()
        .id(version.getId())
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
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

  public static InformationDeskVersion toEntity(InformationDeskVersionModel model){
    return InformationDeskVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .number(new Sloid(model.getParentServicePointSloid()).getServicePointNumber())
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .designation(model.getDesignation())
        .additionalInformation(model.getAdditionalInformation())
        .inductionLoop(model.getInductionLoop())
        .openingHours(model.getOpeningHours())
        .wheelchairAccess(model.getWheelchairAccess())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .version(model.getEtagVersion())
        .build();
  }

}
