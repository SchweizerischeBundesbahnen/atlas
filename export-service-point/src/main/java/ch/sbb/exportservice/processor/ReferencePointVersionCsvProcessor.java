package ch.sbb.exportservice.processor;

import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
import ch.sbb.exportservice.entity.ReferencePointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ReferencePointVersionCsvProcessor implements
    ItemProcessor<ReferencePointVersion, ReferencePointVersionCsvModel> {

  @Override
  public ReferencePointVersionCsvModel process(ReferencePointVersion version) {
    return ReferencePointVersionCsvModel.builder()
        .sloid(version.getSloid())
        .parentSloidServicePoint(version.getParentServicePointSloid())
        .parentNumberServicePoint(version.getParentServicePointNumber().getNumber())
        .designation(version.getDesignation())
        .mainReferencePoint(version.isMainReferencePoint())
        .additionalInformation(version.getAdditionalInformation())
        .rpType(version.getReferencePointType().toString())
        .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .build();
  }

}
