package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel;
import ch.sbb.atlas.export.model.prm.PlatformVersionCsvModel.Fields;
import org.springframework.stereotype.Component;

@Component
public class CsvPlatformVersionWriter extends BaseCsvWriter<PlatformVersionCsvModel>{

    @Override
    String[] getCsvHeader() {
        return new String[]{
                Fields.sloid, Fields.parentSloidServicePoint, Fields.parentNumberServicePoint, Fields.boardingDevice,
                Fields.adviceAccessInfo, Fields.additionalInformation, Fields.contrastingAreas, Fields.dynamicAudio,
                Fields.dynamicVisual, Fields.height, Fields.inclination, Fields.inclinationLongitudal, Fields.inclinationWidth,
                Fields.infoOpportunities, Fields.levelAccessWheelchair, Fields.partialElevation, Fields.superElevation, Fields.tactileSystems,
                Fields.vehicleAccess, Fields.wheelchairAreaLength, Fields.wheelChairAreaWidth, Fields.validFrom, Fields.validTo,
                Fields.creationDate, Fields.creator, Fields.editionDate, Fields.editor
        };
    }
}
