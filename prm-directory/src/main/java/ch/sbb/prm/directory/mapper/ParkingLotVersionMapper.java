package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.RecordingStatus;
import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotOverviewModel;
import ch.sbb.atlas.api.prm.model.parkinglot.ParkingLotVersionModel;
import ch.sbb.atlas.api.prm.model.parkinglot.ReadParkingLotVersionModel;
import ch.sbb.atlas.location.SloidHelper;
import ch.sbb.prm.directory.entity.ParkingLotVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ParkingLotVersionMapper {

  public static ReadParkingLotVersionModel toModel(ParkingLotVersion version) {
    return ReadParkingLotVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .designation(version.getDesignation())
        .additionalInformation(version.getAdditionalInformation())
        .placesAvailable(version.getPlacesAvailable())
        .prmPlacesAvailable(version.getPrmPlacesAvailable())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

  public static ParkingLotOverviewModel toOverviewModel(ParkingLotVersion version) {
    return ParkingLotOverviewModel.builder()
        .recordingStatus(getRecordingStatus(version))
        .id(version.getId())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .designation(version.getDesignation())
        .additionalInformation(version.getAdditionalInformation())
        .placesAvailable(version.getPlacesAvailable())
        .prmPlacesAvailable(version.getPrmPlacesAvailable())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

  static RecordingStatus getRecordingStatus(ParkingLotVersion version) {
    if (version.getPlacesAvailable() == BooleanOptionalAttributeType.TO_BE_COMPLETED
        || version.getPrmPlacesAvailable() == BooleanOptionalAttributeType.TO_BE_COMPLETED) {
      return RecordingStatus.INCOMPLETE;
    }
    return RecordingStatus.COMPLETE;
  }

  public static ParkingLotVersion toEntity(ParkingLotVersionModel model) {
    return ParkingLotVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .number(SloidHelper.getServicePointNumber(model.getParentServicePointSloid()))
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .designation(model.getDesignation())
        .additionalInformation(model.getAdditionalInformation())
        .placesAvailable(model.getPlacesAvailable())
        .prmPlacesAvailable(model.getPrmPlacesAvailable())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .version(model.getEtagVersion())
        .build();
  }

}
