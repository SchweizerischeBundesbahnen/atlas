package ch.sbb.atlas.imports.prm.stoppoint.mapper;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.stoppoint.CreateStopPointVersionModel;
import ch.sbb.atlas.api.prm.model.stoppoint.CreateStopPointVersionModel.CreateStopPointVersionModelBuilder;
import ch.sbb.atlas.imports.prm.stoppoint.StopPointCsvModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPointCsvToModelMapper {

  public static CreateStopPointVersionModel toModel(StopPointCsvModel csvModel){
    CreateStopPointVersionModelBuilder<?, ?> builder = CreateStopPointVersionModel.builder();
        builder.sloid(csvModel.getDsSloid());
        builder.numberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(csvModel.getDidokCode()));
        builder.validFrom(csvModel.getValidFrom());
        builder.validTo(csvModel.getValidTo());
        builder.meansOfTransport(mapToMeansOfTransport(csvModel.getTransportationMeans()));
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
        builder.editionDate(csvModel.getModifiedAt());
    return builder.build();

  }
  StandardAttributeType mapStandardAttributeType(Integer standardAttributeTypeCode){
    return standardAttributeTypeCode!=null ? StandardAttributeType.from(standardAttributeTypeCode) : null;
  }
  boolean mapInteroperable(Integer interoperable){
    if(interoperable == null || interoperable.equals(0)){
      return false;
    }
    return true;
  }

  List<MeanOfTransport> mapToMeansOfTransport(String csvMeansOfTransports){
    List<MeanOfTransport> meansOfTransports;
    String meansOfTransportCode = csvMeansOfTransports.substring(1, csvMeansOfTransports.length() - 1);
    meansOfTransports = Arrays.stream(meansOfTransportCode.split("~")).map(MeanOfTransport::from).collect(Collectors.toList());
    return meansOfTransports;
  }

}
