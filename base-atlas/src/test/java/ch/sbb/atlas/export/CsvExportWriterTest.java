package ch.sbb.atlas.export;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class CsvExportWriterTest {

  @Test
  void shouldWriteCsvFileWithBom() throws IOException {
    //Given
    AtlasCsvMapper csvMapper = new AtlasCsvMapper(DummyCsvModel.class);
    String expectedCsv = """
        dateValue;value
        "2020-12-31";stringValue
        """;
    DummyCsvModel model = new DummyCsvModel("stringValue", LocalDate.of(2020, 12, 31));

    //When
    File file = CsvExportWriter.writeToFile("csvFileWithBom", List.of(model), csvMapper.getObjectWriter());
    Path filePath = file.toPath();

    //Then
    String result = Files.readString(filePath);
    assertThat(result).isEqualTo(CsvExportWriter.UTF_8_BYTE_ORDER_MARK + expectedCsv);
    Files.delete(filePath);
  }
}