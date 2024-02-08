package ch.sbb.prm.directory.mapper;

import static ch.sbb.atlas.servicepoint.Country.SWITZERLAND;

import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.api.prm.model.stoppoint.StopPointVersionModel;
import ch.sbb.atlas.location.SloidHelper;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.ServicePointNonSwissCountryNotAllowedException;
import java.util.HashSet;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class StopPointVersionMapper {

  public static ReadStopPointVersionModel toModel(StopPointVersion version){
    return ReadStopPointVersionModel.builder()
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
        .assistanceCondition(version.getAssistanceCondition())
        .assistanceService(version.getAssistanceService())
        .audioTicketMachine(version.getAudioTicketMachine())
        .additionalInformation(version.getAdditionalInformation())
        .dynamicAudioSystem(version.getDynamicAudioSystem())
        .dynamicOpticSystem(version.getDynamicOpticSystem())
        .infoTicketMachine(version.getInfoTicketMachine())
        .interoperable(version.getInteroperable())
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
        .isReduced(version.isReduced())
        .build();
  }
  public static StopPointVersion toEntity(StopPointVersionModel model){
    return StopPointVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .number(convertToServicePointNumber(model))
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
        .assistanceCondition(model.getAssistanceCondition())
        .assistanceService(model.getAssistanceService())
        .audioTicketMachine(model.getAudioTicketMachine())
        .additionalInformation(model.getAdditionalInformation())
        .dynamicAudioSystem(model.getDynamicAudioSystem())
        .dynamicOpticSystem(model.getDynamicOpticSystem())
        .infoTicketMachine(model.getInfoTicketMachine())
        .interoperable(model.getInteroperable())
        .url(model.getUrl())
        .visualInfo(model.getVisualInfo())
        .wheelchairTicketMachine(model.getWheelchairTicketMachine())
        .assistanceRequestFulfilled(model.getAssistanceRequestFulfilled())
        .ticketMachine(model.getTicketMachine())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .version(model.getEtagVersion())
        .build();
  }

  private static ServicePointNumber convertToServicePointNumber(StopPointVersionModel stopPointVersion) {
    ServicePointNumber servicePointNumber = SloidHelper.getServicePointNumber(stopPointVersion.getSloid());
    if (!SWITZERLAND.equals(servicePointNumber.getCountry())) {
      throw new ServicePointNonSwissCountryNotAllowedException(servicePointNumber);
    }
    return servicePointNumber;
  }

}
