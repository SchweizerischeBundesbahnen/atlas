package ch.sbb.prm.directory.mapper;

import ch.sbb.prm.directory.controller.model.InformationDeskVersionModel;
import ch.sbb.prm.directory.entity.InformationDeskVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InformationDeskVersionMapper {

  public static InformationDeskVersionModel toModel(InformationDeskVersion version){
    return InformationDeskVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .designation(version.getDesignation())
        .info(version.getInfo())
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

}
