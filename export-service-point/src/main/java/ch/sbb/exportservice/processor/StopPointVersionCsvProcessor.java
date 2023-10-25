package ch.sbb.exportservice.processor;

import ch.sbb.exportservice.entity.StopPointVersion;
import ch.sbb.exportservice.model.StopPointVersionCsvModel;
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
        .checkDigit(version.getNumber().getCheckDigit())
        .validFrom(DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER.format(version.getValidTo()))
        .designation(version.getDesignation())
        .meansOfTransport(version.getMeansOfTransportPipeList())
        .creationDate(LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .build();

  }

}
