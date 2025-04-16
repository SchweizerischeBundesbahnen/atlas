package ch.sbb.prm.directory.mapper;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.platform.PlatformVersionModel;
import ch.sbb.atlas.api.prm.model.platform.ReadPlatformVersionModel;
import ch.sbb.atlas.location.SloidHelper;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.servicepoint.enumeration.MeanOfTransport;
import ch.sbb.prm.directory.entity.PlatformVersion;
import ch.sbb.prm.directory.validation.PrmMeansOfTransportHelper;
import java.time.LocalDate;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PlatformVersionMapper {

  public static ReadPlatformVersionModel toModel(PlatformVersion version){
    return ReadPlatformVersionModel.builder()
        .id(version.getId())
        .status(version.getStatus())
        .sloid(version.getSloid())
        .parentServicePointSloid(version.getParentServicePointSloid())
        .number(version.getNumber())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .contrastingAreas(version.getContrastingAreas())
        .boardingDevice(version.getBoardingDevice())
        .additionalInformation(version.getAdditionalInformation())
        .shuttle(version.getShuttle())
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
        .attentionField(version.getAttentionField())
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
  public static PlatformVersion toEntity(PlatformVersionModel model){
    return PlatformVersion.builder()
        .id(model.getId())
        .sloid(model.getSloid())
        .parentServicePointSloid(model.getParentServicePointSloid())
        .number(SloidHelper.getServicePointNumber(model.getParentServicePointSloid()))
        .validFrom(model.getValidFrom())
        .validTo(model.getValidTo())
        .contrastingAreas(model.getContrastingAreas())
        .boardingDevice(model.getBoardingDevice())
        .additionalInformation(model.getAdditionalInformation())
        .shuttle(model.getShuttle())
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
        .attentionField(model.getAttentionField())
        .vehicleAccess(model.getVehicleAccess())
        .wheelchairAreaLength(model.getWheelchairAreaLength())
        .wheelchairAreaWidth(model.getWheelchairAreaWidth())
        .creator(model.getCreator())
        .creationDate(model.getCreationDate())
        .editor(model.getEditor())
        .editionDate(model.getEditionDate())
        .version(model.getEtagVersion())
        .build();
  }

  public static PlatformVersion resetToDefaultValue(PlatformVersion platformVersion, LocalDate validFrom, LocalDate validTo,
      Set<MeanOfTransport> newMeansOfTransport) {
    PlatformVersion resettedVersion = PlatformVersion.builder()
        .sloid(platformVersion.getSloid())
        .status(Status.VALIDATED)
        .parentServicePointSloid(platformVersion.getParentServicePointSloid())
        .number(SloidHelper.getServicePointNumber(platformVersion.getParentServicePointSloid()))
        .shuttle(BooleanOptionalAttributeType.TO_BE_COMPLETED)
        .validFrom(validFrom)
        .validTo(validTo)
        .creator(platformVersion.getCreator())
        .creationDate(platformVersion.getCreationDate())
        .editor(platformVersion.getEditor())
        .editionDate(platformVersion.getEditionDate())
        .build();
    if (PrmMeansOfTransportHelper.isReduced(newMeansOfTransport)) {
      resettedVersion.setTactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED);
      resettedVersion.setVehicleAccess(VehicleAccessAttributeType.TO_BE_COMPLETED);
      if (PrmMeansOfTransportHelper.isAttentionFieldAllowed(newMeansOfTransport)) {
        resettedVersion.setAttentionField(BooleanOptionalAttributeType.TO_BE_COMPLETED);
      }
    } else {
      resettedVersion.setContrastingAreas(BooleanOptionalAttributeType.TO_BE_COMPLETED);
      resettedVersion.setBoardingDevice(BoardingDeviceAttributeType.TO_BE_COMPLETED);
      resettedVersion.setDynamicAudio(BasicAttributeType.TO_BE_COMPLETED);
      resettedVersion.setDynamicVisual(BasicAttributeType.TO_BE_COMPLETED);
      resettedVersion.setLevelAccessWheelchair(BasicAttributeType.TO_BE_COMPLETED);
    }
    return resettedVersion;
  }

  public static void initDefaultDropdownData(PlatformVersion platformVersion, Set<MeanOfTransport> meansOfTransport) {
    if (platformVersion.getShuttle() == null) {
      platformVersion.setShuttle(BooleanOptionalAttributeType.TO_BE_COMPLETED);
    }
    if (PrmMeansOfTransportHelper.isReduced(meansOfTransport)) {
      if (platformVersion.getTactileSystem() == null) {
        platformVersion.setTactileSystem(BooleanOptionalAttributeType.TO_BE_COMPLETED);
      }
      if (platformVersion.getAttentionField() == null && PrmMeansOfTransportHelper.isAttentionFieldAllowed(meansOfTransport)) {
        platformVersion.setAttentionField(BooleanOptionalAttributeType.TO_BE_COMPLETED);
      }
      if (platformVersion.getVehicleAccess() == null) {
        platformVersion.setVehicleAccess(VehicleAccessAttributeType.TO_BE_COMPLETED);
      }
      if (platformVersion.getInfoOpportunities() == null || platformVersion.getInfoOpportunities().isEmpty()) {
        platformVersion.setInfoOpportunities(Set.of(InfoOpportunityAttributeType.TO_BE_COMPLETED));
      }
    } else {
      if (platformVersion.getContrastingAreas() == null) {
        platformVersion.setContrastingAreas(BooleanOptionalAttributeType.TO_BE_COMPLETED);
      }
      if (platformVersion.getBoardingDevice() == null) {
        platformVersion.setBoardingDevice(BoardingDeviceAttributeType.TO_BE_COMPLETED);
      }
      if (platformVersion.getDynamicAudio() == null) {
        platformVersion.setDynamicAudio(BasicAttributeType.TO_BE_COMPLETED);
      }
      if (platformVersion.getDynamicVisual() == null) {
        platformVersion.setDynamicVisual(BasicAttributeType.TO_BE_COMPLETED);
      }
      if (platformVersion.getLevelAccessWheelchair() == null) {
        platformVersion.setLevelAccessWheelchair(BasicAttributeType.TO_BE_COMPLETED);
      }
    }
  }
}
