package ch.sbb.prm.directory.mapper;

import ch.sbb.prm.directory.controller.model.TicketCounterVersionModel;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TicketCounterVersionMapper {

  public static TicketCounterVersionModel toModel(TicketCounterVersion version){
    return TicketCounterVersionModel.builder()
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
