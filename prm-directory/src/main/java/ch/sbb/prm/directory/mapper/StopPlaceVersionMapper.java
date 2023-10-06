package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.controller.model.stopplace.CreateStopPlaceVersionModel;
import ch.sbb.prm.directory.controller.model.stopplace.ReadStopPlaceVersionModel;
import ch.sbb.prm.directory.entity.StopPlaceVersion;
import java.util.HashSet;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPlaceVersionMapper {

  public static ReadStopPlaceVersionModel toModel(StopPlaceVersion version){
    return ReadStopPlaceVersionModel.builder()
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
  public static StopPlaceVersion toEntity(CreateStopPlaceVersionModel model){
    return StopPlaceVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(model.getNumberWithoutCheckDigit()))
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .meansOfTransport(new HashSet<>(model.getMeansOfTransport()))
        .freeText(model.getFreeText())
        .address(model.getAddress())
        .zipCode(model.getZipCode())
        .city(model.getCity())
        .alternativeTransport(model.getAlternativeTransport())
        .alternativeTransportCondition(model.getAlternativeTransportCondition())
        .assistanceAvailability(model.getAssistanceAvailability())
        .alternativeCondition(model.getAlternativeCondition())
        .assistanceService(model.getAssistanceService())
        .audioTicketMachine(model.getAudioTicketMachine())
        .additionalInfo(model.getAdditionalInfo())
        .dynamicAudioSystem(model.getDynamicAudioSystem())
        .dynamicOpticSystem(model.getDynamicOpticSystem())
        .infoTicketMachine(model.getInfoTicketMachine())
        .interoperable(model.isInteroperable())
        .url(model.getUrl())
        .visualInfo(model.getVisualInfo())
        .wheelchairTicketMachine(model.getWheelchairTicketMachine())
        .assistanceRequestFulfilled(model.getAssistanceRequestFulfilled())
        .ticketMachine(model.getTicketMachine())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .build();
  }

}
