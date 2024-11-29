package ch.sbb.line.directory.model.csv;

import static org.assertj.core.api.Assertions.assertThat;

import ch.sbb.line.directory.SublineTestData;
import org.junit.jupiter.api.Test;

class SublineVersionCsvModelTest {

  @Test
  void shouldExportMainlineSlnid() {
    SublineVersionCsvModel sublineVersionCsvModel = SublineVersionCsvModel.toCsvModel(SublineTestData.sublineVersion());
    assertThat(sublineVersionCsvModel.getMainlineSlnid()).isNotNull().isEqualTo("ch:1:slnid:1000546");
  }

}