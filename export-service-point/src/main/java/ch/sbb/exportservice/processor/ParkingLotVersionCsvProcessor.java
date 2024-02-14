package ch.sbb.exportservice.processor;

import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel;
import ch.sbb.exportservice.entity.ParkingLotVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ParkingLotVersionCsvProcessor implements
    ItemProcessor<ParkingLotVersion, ParkingLotVersionCsvModel> {

  @Override
  public ParkingLotVersionCsvModel process(ParkingLotVersion version) {
    return ParkingLotVersionCsvModel.builder()
        .sloid(version.getSloid())
        .parentSloidServicePoint(version.getParentServicePointSloid())
        .parentNumberServicePoint(version.getParentServicePointNumber().getNumber())
        .designation(version.getDesignation())
        .additionalInformation(version.getAdditionalInformation())
        .placesAvailable(version.getPlacesAvailable())
        .prmPlacesAvailable(version.getPrmPlacesAvailable())
        .validFrom(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(BaseServicePointProcessor.DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(BaseServicePointProcessor.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .build();
  }

}
