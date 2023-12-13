package ch.sbb.exportservice.processor;

import ch.sbb.atlas.api.prm.enumeration.BooleanOptionalAttributeType;
import ch.sbb.atlas.api.prm.enumeration.StandardAttributeType;
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
            //.boardingDevice(mapStandardAttributeType(version.getBoardingDevice()))

            .build();
    }

    private String mapStandardAttributeType(StandardAttributeType attributeType){
        return attributeType != null ? attributeType.toString() : null;
    }

    private String mapBooleanOptionalAttributeType(BooleanOptionalAttributeType booleanOptionalAttributeType){
        return booleanOptionalAttributeType != null ? booleanOptionalAttributeType.toString() : null;
    }
}
