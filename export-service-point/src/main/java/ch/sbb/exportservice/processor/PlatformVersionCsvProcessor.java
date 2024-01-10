package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.enumeration.BasicAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BoardingDeviceAttributeType;
import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
import ch.sbb.atlas.api.prm.enumeration.VehicleAccessAttributeType;
import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.exportservice.entity.PlatformVersion;
import ch.sbb.exportservice.reader.RowMapperUtil;
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
            .height(version.getHeight().toString())
            .inclination(version.getInclination().toString())
            .inclinationLongitudal(version.getInclinationLongitudinal().toString())
            .inclinationWidth(version.getInclinationWidth().toString())
            .infoOpportunities(RowMapperUtil.toPipedString(version.getInfoOpportunities()))
            .levelAccessWheelchair(mapBasicAttributeType(version.getLevelAccessWheelchair()))
            .partialElevation(version.isPartialElevation())
            .superElevation(version.getSuperElevation().toString())
            .tactileSystems(mapBooleanOptionalAttributeType(version.getTactileSystems()))
            .vehicleAccess(mapVehicleAccessAttributeType(version.getVehicleAccess()))
            .wheelchairAreaLength(version.getWheelchairAreaLength().toString())
            .wheelChairAreaWidth(version.getWheelChairAreaWidth().toString())
            .validFrom(version.getValidFrom().toString())
            .validTo(version.getValidTo().toString())
            .creationDate(version.getCreationDate().toString())
            .creator(version.getCreator())
            .editionDate(version.getEditionDate().toString())
            .editor(version.getEditor())
            .build();
    }

    private String mapStandardAttributeType(StandardAttributeType attributeType){
        return attributeType != null ? attributeType.toString() : null;
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
