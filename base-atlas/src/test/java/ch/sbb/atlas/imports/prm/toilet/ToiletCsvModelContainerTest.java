package ch.sbb.atlas.imports.prm.toilet;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.sbb.atlas.api.prm.model.toilet.ToiletVersionModel;
import ch.sbb.atlas.testdata.prm.ToiletCsvTestData;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class ToiletCsvModelContainerTest {

  @Test
  void testGetCreateModels() {
    ToiletCsvModel csvModel1 = ToiletCsvTestData.getCsvModel();
    ToiletCsvModel csvModel2 = ToiletCsvTestData.getCsvModel();

    List<ToiletCsvModel> csvModels = Arrays.asList(csvModel1, csvModel2);

    ToiletCsvModelContainer container = new ToiletCsvModelContainer();
    container.setCsvModels(csvModels);

    List<ToiletVersionModel> createModels = container.getCreateModels();

    assertEquals(csvModels.size(), createModels.size());
  }
}