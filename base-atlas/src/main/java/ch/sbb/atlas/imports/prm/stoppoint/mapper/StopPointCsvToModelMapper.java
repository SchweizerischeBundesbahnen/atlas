package ch.sbb.atlas.imports.prm.stoppoint.mapper;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.stoppoint.StopPointVersionModel;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointCsvToModelMapper {

  public static StopPointVersionModel toModel(StopPointCsvModel csvModel) {
    return StopPointVersionModel.builder()
        .sloid(csvModel.getSloid())
        .validFrom(csvModel.getValidFrom())
        .validTo(csvModel.getValidTo())
        .meansOfTransport(MeanOfTransport.fromCode(csvModel.getTransportationMeans()).stream().toList())
        .freeText(csvModel.getFreeText())
        .address(csvModel.getAddress())
        .zipCode(csvModel.getZipCode())
        .city(csvModel.getCity())
        .alternativeTransport(mapStandardAttributeType(csvModel.getAlternativeTransport()))
        .alternativeTransportCondition(csvModel.getAlternativeTransportCondition())
        .assistanceAvailability(mapStandardAttributeType(csvModel.getAssistanceAvailability()))
        .assistanceCondition(csvModel.getAssistanceCondition())
        .assistanceService(mapStandardAttributeType(csvModel.getAssistanceService()))
        .audioTicketMachine(mapStandardAttributeType(csvModel.getAudioTickMach()))
        .additionalInformation(csvModel.getCompInfos())
        .dynamicAudioSystem(mapStandardAttributeType(csvModel.getDynamicAudioSys()))
        .dynamicOpticSystem(mapStandardAttributeType(csvModel.getDynamicOpticSys()))
        .infoTicketMachine(csvModel.getInfoTickMach())
        .interoperable(mapInteroperable(csvModel.getInteroperable()))
        .url(csvModel.getUrl())
        .visualInfo(mapStandardAttributeType(csvModel.getVisualInfos()))
        .wheelchairTicketMachine(mapStandardAttributeType(csvModel.getWheelchairTickMach()))
        .assistanceRequestFulfilled(mapBooleanOptionalAttributeType(csvModel.getAssistanceReqsFulfilled()))
        .ticketMachine(mapBooleanOptionalAttributeType(csvModel.getTicketMachine()))
        .creationDate(csvModel.getCreatedAt())
        .creator(csvModel.getAddedBy())
        .editionDate(csvModel.getModifiedAt())
        .editor(csvModel.getModifiedBy())
        .build();

  }

  StandardAttributeType mapStandardAttributeType(Integer standardAttributeTypeCode) {
    return standardAttributeTypeCode != null ? StandardAttributeType.from(standardAttributeTypeCode) : null;
  }

  BooleanOptionalAttributeType mapBooleanOptionalAttributeType(Integer standardAttributeTypeCode) {
    return standardAttributeTypeCode != null ? BooleanOptionalAttributeType.of(standardAttributeTypeCode) : null;
  }

  Boolean mapInteroperable(Integer interoperable) {
    if (interoperable == null) {
      return null;
    }
    return !interoperable.equals(0);
  }

}
