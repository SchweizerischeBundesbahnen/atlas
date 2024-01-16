package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.api.prm.model.ticketcounter.ReadTicketCounterVersionModel;
import ch.sbb.atlas.api.prm.model.ticketcounter.TicketCounterVersionModel;
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

  public static TicketCounterVersion toEntity(TicketCounterVersionModel model){
    return TicketCounterVersion.builder()
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
