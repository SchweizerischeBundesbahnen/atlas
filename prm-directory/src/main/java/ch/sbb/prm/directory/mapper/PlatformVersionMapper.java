package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.prm.directory.controller.model.platform.CreatePlatformVersionModel;
import ch.sbb.prm.directory.controller.model.platform.ReadPlatformVersionModel;
import ch.sbb.prm.directory.entity.PlatformVersion;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformVersionMapper {

  public static ReadPlatformVersionModel toModel(PlatformVersion version){
    return ReadPlatformVersionModel.builder()
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
  public static PlatformVersion toEntity(CreatePlatformVersionModel model){
    return PlatformVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .number(ServicePointNumber.ofNumberWithoutCheckDigit(model.getNumberWithoutCheckDigit()))
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .boardingDevice(model.getBoardingDevice())
        .additionalInfo(model.getAdditionalInfo())
        .adviceAccessInfo(model.getAdviceAccessInfo())
        .dynamicAudio(model.getDynamicAudio())
        .dynamicVisual(model.getDynamicVisual())
        .height(model.getHeight())
        .inclination(model.getInclination())
        .inclinationLongitudinal(model.getInclinationLongitudinal())
        .inclinationWidth(model.getInclinationWidth())
        .infoOpportunities(Set.copyOf(model.getInfoOpportunities()))
        .levelAccessWheelchair(model.getLevelAccessWheelchair())
        .partialElevation(model.getPartialElevation())
        .superelevation(model.getSuperelevation())
        .tactileSystem(model.getTactileSystem())
        .vehicleAccess(model.getVehicleAccess())
        .wheelchairAreaLength(model.getWheelchairAreaLength())
        .wheelchairAreaWidth(model.getWheelchairAreaWidth())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .build();
  }

}
