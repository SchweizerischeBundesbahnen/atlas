package ch.sbb.exportservice.processor;

import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
import ch.sbb.exportservice.entity.ReferencePointVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ReferencePointVersionCsvProcessor extends BaseServicePointProcessor implements
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
        .validFrom(DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .creator(version.getCreator())
        .editionDate(LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .editor(version.getEditor())
        .build();
  }

}
