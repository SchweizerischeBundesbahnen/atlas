package ch.sbb.exportservice.job.toilet;

import ch.sbb.atlas.export.model.prm.ToiletVersionCsvModel;
import ch.sbb.exportservice.job.BaseServicePointProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ToiletVersionCsvProcessor extends BaseServicePointProcessor implements
    ItemProcessor<ToiletVersion, ToiletVersionCsvModel> {

  @Override
  public ToiletVersionCsvModel process(ToiletVersion version) {
    return ToiletVersionCsvModel.builder()
        .sloid(version.getSloid())
        .parentSloidServicePoint(version.getParentServicePointSloid())
        .parentNumberServicePoint(version.getParentServicePointNumber().getNumber())
        .designation(version.getDesignation())
        .wheelchairToilet(mapStandardAttributeType(version.getWheelchairToilet()))
        .additionalInformation(version.getAdditionalInformation())
        .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

}
