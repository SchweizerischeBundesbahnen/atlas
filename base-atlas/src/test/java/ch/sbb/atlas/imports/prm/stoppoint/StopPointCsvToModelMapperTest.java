package ch.sbb.atlas.imports.prm.stoppoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.sbb.atlas.api.prm.model.stoppoint.StopPointVersionModel;
import ch.sbb.atlas.imports.prm.stoppoint.mapper.StopPointCsvToModelMapper;
import ch.sbb.atlas.testdata.prm.StopPointCsvTestData;
import org.junit.jupiter.api.Test;
class StopPointCsvToModelMapperTest {

    @Test
    void testToModel() {
        StopPointCsvModel csvModel = StopPointCsvTestData.getStopPointCsvModel();
        StopPointVersionModel result = StopPointCsvToModelMapper.toModel(csvModel);

        assertEquals(csvModel.getSloid(), result.getSloid());
    }
}
