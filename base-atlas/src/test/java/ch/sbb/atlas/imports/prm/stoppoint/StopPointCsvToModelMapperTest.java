package ch.sbb.atlas.imports.prm.stoppoint;

import ch.sbb.atlas.api.prm.model.stoppoint.CreateStopPointVersionModel;
import ch.sbb.atlas.imports.prm.stoppoint.mapper.StopPointCsvToModelMapper;
import ch.sbb.atlas.testdata.prm.StopPointCsvTestData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
class StopPointCsvToModelMapperTest {

    @Test
    void testToModel() {
        StopPointCsvModel csvModel = StopPointCsvTestData.getStopPointCsvModel();
        CreateStopPointVersionModel result = StopPointCsvToModelMapper.toModel(csvModel);

        assertEquals(csvModel.getSloid(), result.getSloid());
    }
}
