package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.controller.model.ticketcounter.CreateTicketCounterVersionModel;
import ch.sbb.prm.directory.controller.model.ticketcounter.ReadTicketCounterVersionModel;
import ch.sbb.prm.directory.entity.TicketCounterVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TicketCounterVersionMapper {

  public static ReadTicketCounterVersionModel toModel(TicketCounterVersion version){
    return ReadTicketCounterVersionModel.builder()
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

  public static TicketCounterVersion toEntity(CreateTicketCounterVersionModel model){
    return TicketCounterVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(model.getNumberWithoutCheckDigit()))
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .designation(model.getDesignation())
        .info(model.getInfo())
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
