package ch.sbb.exportservice.job.prm.referencepoint;

import ch.sbb.atlas.export.model.prm.ReferencePointVersionCsvModel;
import ch.sbb.exportservice.utile.MapperUtil;
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
        .referencePointType(version.getReferencePointType().toString())
        .validFrom(MapperUtil.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(MapperUtil.DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

}
