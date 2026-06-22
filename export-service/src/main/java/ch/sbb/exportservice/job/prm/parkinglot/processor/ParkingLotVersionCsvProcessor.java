package ch.sbb.exportservice.job.prm.parkinglot.processor;

import static ch.sbb.atlas.helper.DateHelper.DATE_FORMATTER_BASE;
import static ch.sbb.exportservice.util.MapperUtil.LOCAL_DATE_TIME_FORMATTER;

import ch.sbb.exportservice.job.prm.parkinglot.entity.ParkingLotVersion;
import ch.sbb.exportservice.job.prm.parkinglot.model.ParkingLotVersionCsvModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.infrastructure.item.ItemProcessor;

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
        .validFrom(DATE_FORMATTER_BASE.format(version.getValidFrom()))
        .validTo(DATE_FORMATTER_BASE.format(version.getValidTo()))
        .creationDate(LOCAL_DATE_TIME_FORMATTER.format(version.getCreationDate()))
        .editionDate(LOCAL_DATE_TIME_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

}
