package ch.sbb.prm.directory.mapper;

import ch.sbb.prm.directory.controller.StopPlaceVersionModel;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPlaceVersionMapper {

  public static StopPlaceVersionModel toModel(StopPlaceVersion version){
    return StopPlaceVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .meansOfTransport(version.getMeansOfTransport().stream().sorted().toList())
        .freeText(version.getFreeText())
        .address(version.getAddress())
        .zipCode(version.getZipCode())
        .city(version.getCity())
        .alternativeTransport(version.getAlternativeTransport())
        .alternativeTransportCondition(version.getAlternativeTransportCondition())
        .assistanceAvailability(version.getAssistanceAvailability())
        .alternativeCondition(version.getAlternativeCondition())
        .assistanceService(version.getAssistanceService())
        .audioTicketMachine(version.getAudioTicketMachine())
        .additionalInfo(version.getAdditionalInfo())
        .dynamicAudioSystem(version.getDynamicAudioSystem())
        .infoTicketMachine(version.getInfoTicketMachine())
        .interoperable(version.isInteroperable())
        .url(version.getUrl())
        .visualInfo(version.getVisualInfo())
        .wheelchairTicketMachine(version.getWheelchairTicketMachine())
        .assistanceRequestFulfilled(version.getAssistanceRequestFulfilled())
        .ticketMachine(version.getTicketMachine())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

}
