package ch.sbb.exportservice.job.prm.stoppoint.processor;

import static ch.sbb.exportservice.util.MapperUtil.mapBooleanOptionalAttributeType;
import static ch.sbb.exportservice.util.MapperUtil.mapStandardAttributeType;

import ch.sbb.exportservice.job.prm.stoppoint.model.StopPointVersionCsvModel;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import ch.sbb.exportservice.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

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
        .recordingObligation(version.isRecordingObligation())
        .checkDigit(version.getNumber().getCheckDigit())
        .validFrom(MapperUtil.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(MapperUtil.DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

}
