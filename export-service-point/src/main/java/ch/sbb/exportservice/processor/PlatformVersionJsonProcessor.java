package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.model.platform.ReadPlatformVersionModel;
import ch.sbb.exportservice.entity.PlatformVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class PlatformVersionJsonProcessor extends BaseServicePointProcessor implements ItemProcessor<PlatformVersion, ReadPlatformVersionModel> {

    @Override
    public ReadPlatformVersionModel process(PlatformVersion version) throws Exception {
        return ReadPlatformVersionModel.builder()
            .id(version.getId())
            .sloid(version.getSloid())
            .parentServicePointSloid(version.getParentSloidServicePoint())
            .number(version.getParentNumberServicePoint())
            .boardingDevice(version.getBoardingDevice())
            .adviceAccessInfo(version.getAdviceAccessInfo())
            .additionalInformation(version.getAdditionalInformation())
            .contrastingAreas(version.getContrastingAreas())
            .dynamicAudio(version.getDynamicAudio())
            .dynamicVisual(version.getDynamicVisual())
            .height(version.getHeight())
            .inclination(version.getInclination())
            .inclinationLongitudinal(version.getInclinationLongitudinal())
            .inclinationWidth(version.getInclinationWidth())
            .infoOpportunities(
                    version.getInfoOpportunities() != null ? version.getInfoOpportunities().stream().toList() : null)
            .levelAccessWheelchair(version.getLevelAccessWheelchair())
            .partialElevation(version.getPartialElevation())
            .superelevation(version.getSuperElevation())
            .tactileSystem(version.getTactileSystems())
            .vehicleAccess(version.getVehicleAccess())
            .wheelchairAreaLength(version.getWheelchairAreaLength())
            .wheelchairAreaWidth(version.getWheelchairAreaWidth())
            .validFrom(version.getValidFrom())
            .validTo(version.getValidTo())
            .creationDate(version.getCreationDate())
            .creator(version.getCreator())
            .editionDate(version.getEditionDate())
            .editor(version.getEditor())
            .etagVersion(version.getVersion())
            .build();
    }
}
