package ch.sbb.prm.directory.mapper;

import static ch.sbb.atlas.servicepoint.Country.SWITZERLAND;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.atlas.api.prm.model.stoppoint.StopPointVersionModel;
import ch.sbb.atlas.location.SloidHelper;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.entity.StopPointVersion;
import ch.sbb.prm.directory.exception.ServicePointNonSwissCountryNotAllowedException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class StopPointVersionMapper {

  public static ReadStopPointVersionModel toModel(StopPointVersion version, Map<String, Boolean> recordingObligations) {
    return toModel(version, recordingObligations.getOrDefault(version.getSloid(), true));
  }

  public static ReadStopPointVersionModel toModel(StopPointVersion version, boolean recordingObligation) {
    return ReadStopPointVersionModel.builder()
        .id(version.getId())
        .status(version.getStatus())
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
        .shuttleService(version.getShuttleService())
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
        .recordingObligation(recordingObligation)
        .build();
  }

  public static StopPointVersion toEntity(StopPointVersionModel model) {
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
        .shuttleService(model.getShuttleService())
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

  public static StopPointVersion resetToDefaultValue(StopPointVersion version,
      LocalDate validFrom, LocalDate validTo, Set<MeanOfTransport> meanOfTransports) {
    return StopPointVersion.builder()
        .sloid(version.getSloid())
        .status(Status.VALIDATED)
        .number(version.getNumber())
        .validFrom(validFrom)
        .validTo(validTo)
        .meansOfTransport(meanOfTransports)
        .alternativeTransport(StandardAttributeType.TO_BE_COMPLETED)
        .shuttleService(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceAvailability(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceService(StandardAttributeType.TO_BE_COMPLETED)
        .audioTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicAudioSystem(StandardAttributeType.TO_BE_COMPLETED)
        .dynamicOpticSystem(StandardAttributeType.TO_BE_COMPLETED)
        .visualInfo(StandardAttributeType.TO_BE_COMPLETED)
        .wheelchairTicketMachine(StandardAttributeType.TO_BE_COMPLETED)
        .assistanceRequestFulfilled(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .ticketMachine(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .build();
  }

}
