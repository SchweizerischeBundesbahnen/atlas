package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.export.model.prm.StopPointVersionCsvModel;
import ch.sbb.exportservice.entity.StopPointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class StopPointVersionCsvProcessor extends BaseServicePointProcessor implements
    ItemProcessor<StopPointVersion, StopPointVersionCsvModel> {

  @Override
  public StopPointVersionCsvModel process(StopPointVersion version) {
    return StopPointVersionCsvModel.builder()
        .sloid(version.getSloid())
        .number(version.getNumber().getNumber())
        .freeText(version.getFreeText())
        .address(version.getAddress())
        .zipCode(version.getZipCode())
        .city(version.getCity())
        .alternativeTransport(mapStandardAttributeType(version.getAlternativeTransport()))
        .alternativeTransportCondition(version.getAlternativeTransportCondition())
        .assistanceAvailability(mapStandardAttributeType(version.getAssistanceAvailability()))
        .assistanceCondition(version.getAssistanceCondition())
        .assistanceService(mapStandardAttributeType(version.getAssistanceService()))
        .audioTicketMachine(mapStandardAttributeType(version.getAudioTicketMachine()))
        .additionalInformation(version.getAdditionalInformation())
        .dynamicAudioSystem(mapStandardAttributeType(version.getDynamicAudioSystem()))
        .dynamicOpticSystem(mapStandardAttributeType(version.getDynamicOpticSystem()))
        .infoTicketMachine(version.getInfoTicketMachine())
        .interoperable(version.getInteroperable())
        .url(version.getUrl())
        .visualInfo(mapStandardAttributeType(version.getVisualInfo()))
        .wheelchairTicketMachine(mapStandardAttributeType(version.getWheelchairTicketMachine()))
        .assistanceRequestFulfilled(mapBooleanOptionalAttributeType(version.getAssistanceRequestFulfilled()))
        .ticketMachine(mapBooleanOptionalAttributeType(version.getTicketMachine()))
        .meansOfTransport(version.getMeansOfTransportPipeList())
        .checkDigit(version.getNumber().getCheckDigit())
        .validFrom(DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .build();

  }

  private String mapStandardAttributeType(StandardAttributeType attributeType){
    return attributeType != null ? attributeType.toString() : null;
  }

  private String mapBooleanOptionalAttributeType(BooleanOptionalAttributeType booleanOptionalAttributeType){
    return booleanOptionalAttributeType != null ? booleanOptionalAttributeType.toString() : null;
  }

}
