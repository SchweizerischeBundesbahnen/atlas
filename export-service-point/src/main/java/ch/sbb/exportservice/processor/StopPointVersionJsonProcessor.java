package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.model.stoppoint.ReadStopPointVersionModel;
import ch.sbb.exportservice.entity.StopPointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class StopPointVersionJsonProcessor extends BaseServicePointProcessor implements ItemProcessor<StopPointVersion,
    ReadStopPointVersionModel> {

  @Override
  public ReadStopPointVersionModel process(StopPointVersion stopPointVersion) {

    return ReadStopPointVersionModel.builder()
        .id(stopPointVersion.getId())
        .number(stopPointVersion.getNumber())
        .sloid(stopPointVersion.getSloid())
        .meansOfTransport(getMeansOfTransportSorted(stopPointVersion.getMeansOfTransport()))
        .validFrom(stopPointVersion.getValidFrom())
        .validTo(stopPointVersion.getValidTo())
        .creationDate(stopPointVersion.getCreationDate())
        .creator(stopPointVersion.getCreator())
        .editionDate(stopPointVersion.getEditionDate())
        .editor(stopPointVersion.getEditor())
        .etagVersion(stopPointVersion.getVersion())
        .build();
  }

}
