package ch.sbb.atlas.imports.prm.referencepoint;

import ch.sbb.atlas.api.prm.model.referencepoint.ReferencePointVersionModel;
import ch.sbb.atlas.testdata.prm.ReferencePointCsvTestData;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReferencePointCsvModelContainerTest {

    @Test
    void testGetCreateModels() {
        ReferencePointCsvModel csvModel1 = ReferencePointCsvTestData.getCsvModel();
        ReferencePointCsvModel csvModel2 = ReferencePointCsvTestData.getCsvModel();

        List<ReferencePointCsvModel> csvModels = Arrays.asList(csvModel1, csvModel2);

        ReferencePointCsvModelContainer container = new ReferencePointCsvModelContainer();
        container.setCsvModels(csvModels);

        List<ReferencePointVersionModel> createModels = container.getCreateModels();

        assertEquals(csvModels.size(), createModels.size());
    }
    
}
