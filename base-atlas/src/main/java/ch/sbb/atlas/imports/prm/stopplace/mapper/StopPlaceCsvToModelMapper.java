package ch.sbb.atlas.imports.prm.stopplace.mapper;

import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.model.stopplace.CreateStopPlaceVersionModel;
import ch.sbb.atlas.api.prm.model.stopplace.CreateStopPlaceVersionModel.CreateStopPlaceVersionModelBuilder;
import ch.sbb.atlas.imports.prm.stopplace.StopPlaceCsvModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StopPlaceCsvToModelMapper {

  public static CreateStopPlaceVersionModel toModel(StopPlaceCsvModel csvModel){
    CreateStopPlaceVersionModelBuilder<?, ?> builder = CreateStopPlaceVersionModel.builder();

        builder.sloid(csvModel.getDsSloid());
        builder.numberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(csvModel.getDidokCode()));
        builder.validFrom(csvModel.getValidFrom());
        builder.validTo(csvModel.getValidTo());
        builder.meansOfTransport(mapToMeansOfTransport(csvModel.getTransportationMeans()));
        builder.freeText(csvModel.getFreeText());
        builder.address(csvModel.getAddress());
        builder.zipCode(csvModel.getZipCode());
        builder.city(csvModel.getCity());
        builder.alternativeTransport(StandardAttributeType.from(csvModel.getAlternativeTransport()));
        builder.alternativeTransportCondition(csvModel.getAlternativeTransportCondition());
        builder.assistanceAvailability(StandardAttributeType.from(csvModel.getAssistanceAvailability()));
        builder.alternativeCondition(csvModel.getAssistanceCondition());
        builder.assistanceService(StandardAttributeType.from(csvModel.getAssistanceService()));
        builder.audioTicketMachine(StandardAttributeType.from(csvModel.getAudioTickMach()));
        builder.additionalInfo(csvModel.getCompInfos());
        builder.dynamicAudioSystem(StandardAttributeType.from(csvModel.getDynamicAudioSys()));
        builder.dynamicOpticSystem(StandardAttributeType.from(csvModel.getDynamicOpticSys()));
        builder.infoTicketMachine(csvModel.getInfoTickMach());
        builder.interoperable(mapInteroperable(csvModel.getInteroperable()));
        builder.url(csvModel.getUrl());
        builder.visualInfo(StandardAttributeType.from(csvModel.getVisualInfos()));
        builder.wheelchairTicketMachine(StandardAttributeType.from(csvModel.getWheelchairTickMach()));
        builder.assistanceRequestFulfilled(StandardAttributeType.from(csvModel.getAssistanceReqsFulfilled()));
        builder.ticketMachine(StandardAttributeType.from(csvModel.getTicketMachine()));
        builder.creationDate(csvModel.getCreatedAt());
        builder.editionDate(csvModel.getModifiedAt());
    return builder.build();

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
