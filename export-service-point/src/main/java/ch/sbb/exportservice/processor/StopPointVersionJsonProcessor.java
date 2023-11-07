package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.exportservice.entity.StopPointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class StopPointVersionJsonProcessor extends BaseServicePointProcessor implements ItemProcessor<StopPointVersion,
    ReadStopPointVersionModel> {

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
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .creationDate(version.getCreationDate())
        .creator(version.getCreator())
        .editionDate(version.getEditionDate())
        .editor(version.getEditor())
        .etagVersion(version.getVersion())
        .build();
  }

}
