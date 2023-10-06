package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.controller.model.create.CreateParkingLotVersionModel;
import ch.sbb.prm.directory.controller.model.read.ReadParkingLotVersionModel;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ParkingLotVersionMapper {

  public static ReadParkingLotVersionModel toModel(ParkingLotVersion version){
    return ReadParkingLotVersionModel.builder()
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

  public static ParkingLotVersion toEntity(CreateParkingLotVersionModel model){
    return ParkingLotVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(model.getNumberWithoutCheckDigit()))
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .designation(model.getDesignation())
        .info(model.getInfo())
        .placesAvailable(model.getPlacesAvailable())
        .prmPlacesAvailable(model.getPrmPlacesAvailable())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .build();
  }

}
