package ch.sbb.atlas.imports.prm.parkinglot;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotVersionModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ParkingLotCsvToModelMapper {

  public ParkingLotVersionModel toModel(ParkingLotCsvModel parkingLotCsvModel) {
    return ParkingLotVersionModel.builder()
        .parentServicePointSloid(parkingLotCsvModel.getDsSloid())
        .sloid(parkingLotCsvModel.getSloid())
        .designation(parkingLotCsvModel.getDescription())
        .additionalInformation(parkingLotCsvModel.getInfo())
        .placesAvailable(BooleanOptionalAttributeType.of(parkingLotCsvModel.getPlacesAvailable()))
        .prmPlacesAvailable(BooleanOptionalAttributeType.of(parkingLotCsvModel.getPrmPlacesAvailable()))
        .validFrom(parkingLotCsvModel.getValidFrom())
        .validTo(parkingLotCsvModel.getValidTo())
        .creationDate(parkingLotCsvModel.getCreatedAt())
        .creator(parkingLotCsvModel.getAddedBy())
        .editionDate(parkingLotCsvModel.getModifiedAt())
        .editor(parkingLotCsvModel.getModifiedBy())
        .build();
  }

}
