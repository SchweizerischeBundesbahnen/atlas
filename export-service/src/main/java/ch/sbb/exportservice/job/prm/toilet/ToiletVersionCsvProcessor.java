package ch.sbb.exportservice.job.prm.toilet;

import static ch.sbb.exportservice.utile.MapperUtil.mapStandardAttributeType;

import ch.sbb.atlas.export.model.prm.ToiletVersionCsvModel;
import ch.sbb.exportservice.utile.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

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
        .validFrom(MapperUtil.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(MapperUtil.DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

}
