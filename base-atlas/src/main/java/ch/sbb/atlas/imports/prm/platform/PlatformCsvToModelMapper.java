package ch.sbb.atlas.imports.prm.platform;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.InfoOpportunityAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.api.prm.model.platform.CreatePlatformVersionModel;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class PlatformCsvToModelMapper {

  public static CreatePlatformVersionModel toModel(PlatformCsvModel csvModel){
    return CreatePlatformVersionModel.builder()
        .numberWithoutCheckDigit(ServicePointNumber.removeCheckDigit(csvModel.getDidokCode()))
        .parentServicePointSloid(csvModel.getDsSloid())
        .sloid(csvModel.getSloid())
        .boardingDevice(BoardingDeviceAttributeType.of(csvModel.getBoardingDevice()))
        .adviceAccessInfo(csvModel.getAccessInfo())
        .additionalInformation(csvModel.getInfos())
        .contrastingAreas(BooleanOptionalAttributeType.of(csvModel.getContrastingAreas()))
        .dynamicAudio(BasicAttributeType.of(csvModel.getDynamicAudio()))
        .dynamicVisual(BasicAttributeType.of(csvModel.getDynamicVisual()))
        .height(csvModel.getHeight())
        .inclination(csvModel.getInclination())
        .inclinationLongitudinal(csvModel.getInclinationLong())
        .inclinationWidth(csvModel.getInclinationWidth())
        .infoOpportunities(getInfoOpportunities(csvModel))
        .levelAccessWheelchair(BasicAttributeType.of(csvModel.getLevelAccessWheelchair()))
        .partialElevation(csvModel.getPartialElev().equals(1))
        .superelevation(csvModel.getSuperelevation())
        .tactileSystem(BooleanOptionalAttributeType.of(csvModel.getTactileSystems()))
        .vehicleAccess(VehicleAccessAttributeType.of(csvModel.getVehicleAccess()))
        .wheelchairAreaLength(csvModel.getWheelchairAreaLength())
        .wheelchairAreaWidth(csvModel.getWheelchairAreaWidth())
        .validFrom(csvModel.getValidFrom())
        .validTo(csvModel.getValidTo())
        .creationDate(csvModel.getCreatedAt())
        .creator(csvModel.getAddedBy())
        .editionDate(csvModel.getModifiedAt())
        .editor(csvModel.getModifiedBy())
        .build();
  }

  private static List<InfoOpportunityAttributeType> getInfoOpportunities(PlatformCsvModel csvModel) {
    if (StringUtils.isBlank(csvModel.getInfoBlinds())) {
      return Collections.emptyList();
    }
    return Stream.of(csvModel.getInfoBlinds().split("~"))
        .filter(StringUtils::isNotBlank)
        .map(Integer::parseInt)
        .map(InfoOpportunityAttributeType::of)
        .toList();
  }

}
