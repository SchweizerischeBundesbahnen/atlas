package ch.sbb.exportservice.job.prm.platform.processor;

import static ch.sbb.exportservice.util.MapperUtil.mapBooleanOptionalAttributeType;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.exportservice.job.prm.platform.entity.PlatformVersion;
import ch.sbb.exportservice.job.prm.platform.model.PlatformVersionCsvModel;
import ch.sbb.exportservice.util.MapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PlatformVersionCsvProcessor implements ItemProcessor<PlatformVersion, PlatformVersionCsvModel> {

  @Override
  public PlatformVersionCsvModel process(PlatformVersion version) {
    return PlatformVersionCsvModel.builder()
        .sloid(version.getSloid())
        .parentSloidServicePoint(version.getParentServicePointSloid())
        .parentNumberServicePoint(version.getParentNumberServicePoint().getNumber())
        .shuttle(mapBooleanOptionalAttributeType(version.getShuttle()))
        .boardingDevice(mapBoardingDeviceAttributeType(version.getBoardingDevice()))
        .adviceAccessInfo(version.getAdviceAccessInfo())
        .additionalInformation(version.getAdditionalInformation())
        .contrastingAreas(mapBooleanOptionalAttributeType(version.getContrastingAreas()))
        .dynamicAudio(mapBasicAttributeType(version.getDynamicAudio()))
        .dynamicVisual(mapBasicAttributeType(version.getDynamicVisual()))
        .height(version.getHeight())
        .inclination(version.getInclination())
        .inclinationLongitudinal(version.getInclinationLongitudinal())
        .inclinationWidth(version.getInclinationWidth())
        .infoOpportunities(version.getInfoOpportunitiesPipeList())
        .levelAccessWheelchair(mapBasicAttributeType(version.getLevelAccessWheelchair()))
        .partialElevation(version.getPartialElevation())
        .superElevation(version.getSuperElevation())
        .tactileSystems(mapBooleanOptionalAttributeType(version.getTactileSystems()))
        .attentionField(mapBooleanOptionalAttributeType(version.getAttentionField()))
        .vehicleAccess(mapVehicleAccessAttributeType(version.getVehicleAccess()))
        .wheelChairAreaLength(version.getWheelchairAreaLength())
        .wheelChairAreaWidth(version.getWheelchairAreaWidth())
        .validFrom(MapperUtil.DATE_FORMATTER.format(version.getValidFrom()))
        .validTo(MapperUtil.DATE_FORMATTER.format(version.getValidTo()))
        .creationDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
        .editionDate(MapperUtil.LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
        .status(version.getStatus())
        .build();
  }

  private String mapBasicAttributeType(BasicAttributeType attributeType) {
    return attributeType != null ? attributeType.toString() : null;
  }

  private String mapBoardingDeviceAttributeType(BoardingDeviceAttributeType attributeType) {
    return attributeType != null ? attributeType.toString() : null;
  }

  private String mapVehicleAccessAttributeType(VehicleAccessAttributeType attributeType) {
    return attributeType != null ? attributeType.toString() : null;
  }

}
