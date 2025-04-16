package ch.sbb.exportservice.job.prm.stoppoint.processor;

import static ch.sbb.exportservice.util.MapperUtil.getMeansOfTransportSorted;

import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.exportservice.job.prm.stoppoint.entity.StopPointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class StopPointVersionJsonProcessor implements ItemProcessor<StopPointVersion, ReadStopPointVersionModel> {

  @Override
  public ReadStopPointVersionModel process(StopPointVersion version) {

    return ReadStopPointVersionModel.builder()
        .id(version.getId())
        .number(version.getNumber())
        .sloid(version.getSloid())
        .meansOfTransport(getMeansOfTransportSorted(version.getMeansOfTransport()))
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
        .interoperable(Boolean.valueOf(version.getInteroperable()))
        .url(version.getUrl())
        .visualInfo(version.getVisualInfo())
        .wheelchairTicketMachine(version.getWheelchairTicketMachine())
        .assistanceRequestFulfilled(version.getAssistanceRequestFulfilled())
        .ticketMachine(version.getTicketMachine())
        .recordingObligation(version.isRecordingObligation())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .creationDate(version.getCreationDate())
        .creator(version.getCreator())
        .editionDate(version.getEditionDate())
        .editor(version.getEditor())
        .etagVersion(version.getVersion())
        .status(version.getStatus())
        .build();
  }

}
