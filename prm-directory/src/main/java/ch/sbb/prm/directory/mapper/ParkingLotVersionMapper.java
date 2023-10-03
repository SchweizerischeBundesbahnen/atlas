package ch.sbb.prm.directory.mapper;

import ch.sbb.prm.directory.controller.ParkingLotVersionModel;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ParkingLotVersionMapper {

  public static ParkingLotVersionModel toModel(ParkingLotVersion version){
    return ParkingLotVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .designation(version.getDesignation())
        .info(version.getInfo())
        .placesAvailable(version.getPlacesAvailable())
        .prmPlacesAvailable(version.getPrmPlacesAvailable())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

}
