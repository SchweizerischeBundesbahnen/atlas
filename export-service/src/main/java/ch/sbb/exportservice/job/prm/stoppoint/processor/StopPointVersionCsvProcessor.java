package ch.sbb.exportservice.job.prm.stoppoint.processor;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_TIME_FORMATTER;
import static ch.sbb.exportservice.util.MapperUtil.mapBooleanOptionalAttributeType;
import static ch.sbb.exportservice.util.MapperUtil.mapStandardAttributeType;

import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import ch.sbb.exportservice.job.prm.stoppoint.model.StopPointVersionCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

@Slf4j
public class StopPointVersionCsvProcessor implements ItemProcessor<StopPointVersion, StopPointVersionCsvModel> {

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
        .shuttleService(mapStandardAttributeType(version.getShuttleService()))
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
        .validFrom(DATE_FORMATTER_BASE.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER_BASE.format(version.getValidTo()))
        .creationDate(LOCAL_DATE_TIME_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_TIME_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

}
