package ch.sbb.atlas.imports.prm.stoppoint.mapper;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.stoppoint.CreateStopPointVersionModel;
import ch.sbb.atlas.api.prm.model.stoppoint.CreateStopPointVersionModel.CreateStopPointVersionModelBuilder;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointCsvToModelMapper {

  public static CreateStopPointVersionModel toModel(StopPointCsvModel csvModel){
    CreateStopPointVersionModelBuilder<?, ?> builder = CreateStopPointVersionModel.builder();
        builder.sloid(csvModel.getSloid());
        builder.numberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(csvModel.getDidokCode()));
        builder.validFrom(csvModel.getValidFrom());
        builder.validTo(csvModel.getValidTo());
        builder.meansOfTransport(MeanOfTransport.fromCode(csvModel.getTransportationMeans()).stream().toList());
        builder.freeText(csvModel.getFreeText());
        builder.address(csvModel.getAddress());
        builder.zipCode(csvModel.getZipCode());
        builder.city(csvModel.getCity());
        builder.alternativeTransport(mapStandardAttributeType(csvModel.getAlternativeTransport()));
        builder.alternativeTransportCondition(csvModel.getAlternativeTransportCondition());
        builder.assistanceAvailability(mapStandardAttributeType(csvModel.getAssistanceAvailability()));
        builder.assistanceCondition(csvModel.getAssistanceCondition());
        builder.assistanceService(mapStandardAttributeType(csvModel.getAssistanceService()));
        builder.audioTicketMachine(mapStandardAttributeType(csvModel.getAudioTickMach()));
        builder.additionalInformation(csvModel.getCompInfos());
        builder.dynamicAudioSystem(mapStandardAttributeType(csvModel.getDynamicAudioSys()));
        builder.dynamicOpticSystem(mapStandardAttributeType(csvModel.getDynamicOpticSys()));
        builder.infoTicketMachine(csvModel.getInfoTickMach());
        builder.interoperable(mapInteroperable(csvModel.getInteroperable()));
        builder.url(csvModel.getUrl());
        builder.visualInfo(mapStandardAttributeType(csvModel.getVisualInfos()));
        builder.wheelchairTicketMachine(mapStandardAttributeType(csvModel.getWheelchairTickMach()));
        builder.assistanceRequestFulfilled(mapStandardAttributeType(csvModel.getAssistanceReqsFulfilled()));
        builder.ticketMachine(mapStandardAttributeType(csvModel.getTicketMachine()));
        builder.creationDate(csvModel.getCreatedAt());
        builder.creator(csvModel.getAddedBy());
        builder.editionDate(csvModel.getModifiedAt());
        builder.editor(csvModel.getModifiedBy());
    return builder.build();

  }
  StandardAttributeType mapStandardAttributeType(Integer standardAttributeTypeCode){
    return standardAttributeTypeCode!=null ? StandardAttributeType.from(standardAttributeTypeCode) : null;
  }
  Boolean mapInteroperable(Integer interoperable){
    if (interoperable == null){
      return null;
    }
    return !interoperable.equals(0);
  }

}
