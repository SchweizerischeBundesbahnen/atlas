package ch.sbb.exportservice.job.prm.referencepoint.processor;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_TIME_FORMATTER;

import ch.sbb.exportservice.job.prm.referencepoint.entity.ReferencePointVersion;
import ch.sbb.exportservice.job.prm.referencepoint.model.ReferencePointVersionCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

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
        .referencePointType(version.getReferencePointType().toString())
        .validFrom(DATE_FORMATTER_BASE.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER_BASE.format(version.getValidTo()))
        .creationDate(LOCAL_DATE_TIME_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_TIME_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

}
