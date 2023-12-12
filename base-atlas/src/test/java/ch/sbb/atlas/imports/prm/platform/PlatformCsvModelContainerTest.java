package ch.sbb.atlas.imports.prm.platform;

import ch.sbb.atlas.api.prm.model.platform.CreatePlatformVersionModel;
import ch.sbb.atlas.testdata.prm.PlatformCsvTestData;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


class PlatformCsvModelContainerTest {

    @Test
    public void testGetCreateModels() {
        PlatformCsvModel csvModel1 = PlatformCsvTestData.getCsvModel();
        PlatformCsvModel csvModel2 = PlatformCsvTestData.getCsvModel();

        List<PlatformCsvModel> csvModels = Arrays.asList(csvModel1, csvModel2);

        PlatformCsvModelContainer container = new PlatformCsvModelContainer();
        container.setCsvModels(csvModels);

        List<CreatePlatformVersionModel> createModels = container.getCreateModels();

        assertEquals(csvModels.size(), createModels.size());
    }

}
