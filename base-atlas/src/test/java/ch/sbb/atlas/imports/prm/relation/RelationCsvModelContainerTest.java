package ch.sbb.atlas.imports.prm.relation;

import ch.sbb.atlas.api.prm.model.relation.RelationVersionModel;
import ch.sbb.atlas.testdata.prm.RelationCsvTestData;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RelationCsvModelContainerTest {

    @Test
    void testGetCreateModels() {
        RelationCsvModel csvModel1 = RelationCsvTestData.getCsvModel();
        RelationCsvModel csvModel2 = RelationCsvTestData.getCsvModel();

        List<RelationCsvModel> csvModels = Arrays.asList(csvModel1, csvModel2);

        RelationCsvModelContainer container = new RelationCsvModelContainer();
        container.setCsvModels(csvModels);

        List<RelationVersionModel> createModels = container.getCreateModels();

        assertEquals(csvModels.size(), createModels.size());
    }
    
}
