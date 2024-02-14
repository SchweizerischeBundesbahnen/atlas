package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.exportservice.entity.ParkingLotVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ParkingLotVersionJsonProcessor extends BaseServicePointProcessor implements ItemProcessor<ParkingLotVersion,
    ReadParkingLotVersionModel> {

  @Override
  public ReadParkingLotVersionModel process(ParkingLotVersion version) {
    return ReadParkingLotVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .number(version.getParentServicePointNumber())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .designation(version.getDesignation())
        .additionalInformation(version.getAdditionalInformation())
        .placesAvailable(version.getPlacesAvailable())
        .prmPlacesAvailable(version.getPrmPlacesAvailable())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .creationDate(version.getCreationDate())
        .creator(version.getCreator())
        .editionDate(version.getEditionDate())
        .editor(version.getEditor())
        .etagVersion(version.getVersion())
        .build();
  }

}
