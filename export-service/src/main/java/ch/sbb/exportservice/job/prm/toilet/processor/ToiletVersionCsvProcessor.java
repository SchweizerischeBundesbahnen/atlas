package ch.sbb.exportservice.job.prm.toilet.processor;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_TIME_FORMATTER;
import static ch.sbb.exportservice.util.MapperUtil.mapStandardAttributeType;

import ch.sbb.exportservice.job.prm.toilet.entity.ToiletVersion;
import ch.sbb.exportservice.job.prm.toilet.model.ToiletVersionCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

@Slf4j
public class ToiletVersionCsvProcessor implements ItemProcessor<ToiletVersion, ToiletVersionCsvModel> {

  @Override
  public ToiletVersionCsvModel process(ToiletVersion version) {
    return ToiletVersionCsvModel.builder()
        .sloid(version.getSloid())
        .parentSloidServicePoint(version.getParentServicePointSloid())
        .parentNumberServicePoint(version.getParentServicePointNumber().getNumber())
        .designation(version.getDesignation())
        .wheelchairToilet(mapStandardAttributeType(version.getWheelchairToilet()))
        .additionalInformation(version.getAdditionalInformation())
        .validFrom(DATE_FORMATTER_BASE.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER_BASE.format(version.getValidTo()))
        .creationDate(LOCAL_DATE_TIME_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_TIME_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

}
