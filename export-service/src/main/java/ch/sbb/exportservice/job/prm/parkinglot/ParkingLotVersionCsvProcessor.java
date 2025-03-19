package ch.sbb.exportservice.job.prm.parkinglot;

import ch.sbb.atlas.export.model.prm.ParkingLotVersionCsvModel;
import ch.sbb.exportservice.utile.MapperUtil;
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
        .validFrom(MapperUtil.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(MapperUtil.DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

}
