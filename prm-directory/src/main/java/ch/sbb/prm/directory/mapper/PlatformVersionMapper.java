package ch.sbb.prm.directory.mapper;

import ch.sbb.prm.directory.controller.PlatformVersionModel;
import ch.sbb.prm.directory.entity.PlatformVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformVersionMapper {

  public static PlatformVersionModel toModel(PlatformVersion version){
    return PlatformVersionModel.builder()
        .id(version.getId())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .boardingDevice(version.getBoardingDevice())
        .additionalInfo(version.getAdditionalInfo())
        .adviceAccessInfo(version.getAdviceAccessInfo())
        .dynamicAudio(version.getDynamicAudio())
        .dynamicVisual(version.getDynamicVisual())
        .height(version.getHeight())
        .inclination(version.getInclination())
        .inclinationLongitudinal(version.getInclinationLongitudinal())
        .inclinationWidth(version.getInclinationWidth())
        .infoOpportunities(version.getInfoOpportunities().stream().toList())
        .levelAccessWheelchair(version.getLevelAccessWheelchair())
        .partialElevation(version.getPartialElevation())
        .superelevation(version.getSuperelevation())
        .tactileSystem(version.getTactileSystem())
        .vehicleAccess(version.getVehicleAccess())
        .wheelchairAreaLength(version.getWheelchairAreaLength())
        .wheelchairAreaWidth(version.getWheelchairAreaWidth())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

}
