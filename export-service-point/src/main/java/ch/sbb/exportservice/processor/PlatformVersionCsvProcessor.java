package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.exportservice.entity.PlatformVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PlatformVersionCsvProcessor extends BaseServicePointProcessor implements
    ItemProcessor<PlatformVersion, PlatformVersionCsvModel> {

    @Override
    public PlatformVersionCsvModel process(PlatformVersion version) {
        return PlatformVersionCsvModel.builder()
            .sloid(version.getSloid())
            .parentSloidServicePoint(version.getParentSloidServicePoint())
            .parentNumberServicePoint(version.getParentNumberServicePoint())
            .boardingDevice(mapBoardingDeviceAttributeType(version.getBoardingDevice()))
            .adviceAccessInfo(version.getAdviceAccessInfo())
            .additionalInformation(version.getAdditionalInformation())
            .contrastingAreas(mapBooleanOptionalAttributeType(version.getContrastingAreas()))
            .dynamicAudio(mapBasicAttributeType(version.getDynamicAudio()))
            .dynamicVisual(mapBasicAttributeType(version.getDynamicVisual()))
            .height(version.getHeight())
            .inclination(version.getInclination())
            .inclinationLongitudal(version.getInclinationLongitudinal())
            .inclinationWidth(version.getInclinationWidth())
            .infoOpportunities(version.getInfoOpportunitiesPipeList())
            .levelAccessWheelchair(mapBasicAttributeType(version.getLevelAccessWheelchair()))
            .partialElevation(version.isPartialElevation())
            .superElevation(version.getSuperElevation())
            .tactileSystems(mapBooleanOptionalAttributeType(version.getTactileSystems()))
            .vehicleAccess(mapVehicleAccessAttributeType(version.getVehicleAccess()))
            .wheelchairAreaLength(version.getWheelchairAreaLength())
            .wheelChairAreaWidth(version.getWheelchairAreaWidth())
            .validFrom(DATE_FORMATTER.format(version.getValidFrom()))
            .validTo(DATE_FORMATTER.format(version.getValidTo()))
            .creationDate(LOCAL_DATE_FORMATTER.format(version.getCreationDate()))
            .creator(version.getCreator())
            .editionDate(LOCAL_DATE_FORMATTER.format(version.getEditionDate()))
            .editor(version.getEditor())
            .build();
    }

    private String mapBasicAttributeType(BasicAttributeType attributeType){
        return attributeType != null ? attributeType.toString() : null;
    }

    private String mapBoardingDeviceAttributeType(BoardingDeviceAttributeType attributeType){
        return attributeType != null ? attributeType.toString() : null;
    }

    private String mapVehicleAccessAttributeType(VehicleAccessAttributeType attributeType){
        return attributeType != null ? attributeType.toString() : null;
    }


    private String mapBooleanOptionalAttributeType(BooleanOptionalAttributeType booleanOptionalAttributeType){
        return booleanOptionalAttributeType != null ? booleanOptionalAttributeType.toString() : null;
    }
}
