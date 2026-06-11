package ch.sbb.atlas.export;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ch.sbb.atlas.export.exception.ExportException;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;

class CsvExportWriterTest {

  @Test
  void shouldWriteCsvFileWithBom() throws IOException {
    // Given
    AtlasCsvMapper csvMapper = new AtlasCsvMapper(DummyCsvModel.class);
    String expectedCsv = """
        dateValue;value
        "2020-12-31";stringValue
        """;
    DummyCsvModel model = new DummyCsvModel("stringValue", LocalDate.of(2020, 12, 31));

    // When
    File file = CsvExportWriter.writeToFile("csvFileWithBom", List.of(model), csvMapper.getObjectWriter());
    Path filePath = file.toPath();

    // Then
    String result = Files.readString(filePath);
    assertThat(result).isEqualTo(CsvExportWriter.UTF_8_BYTE_ORDER_MARK + expectedCsv);
    Files.delete(filePath);
  }

  @Test
  void shouldCreateTimestampedCsvFileName() throws IOException {
    // Given
    AtlasCsvMapper csvMapper = new AtlasCsvMapper(DummyCsvModel.class);
    DummyCsvModel model = new DummyCsvModel("value", LocalDate.of(2024, 1, 1));
    Path tempDirectory = Files.createTempDirectory("csv-export-writer-test");
    String outputPrefix = tempDirectory.resolve("statements-export").toString();

    // When
    File file = CsvExportWriter.writeToFile(outputPrefix, List.of(model), csvMapper.getObjectWriter());

    // Then
    assertThat(file.getName()).matches("statements-export_\\d{4}-\\d{2}-\\d{2}_\\d{2}-\\d{2}-\\d{2}.csv");
    Files.deleteIfExists(file.toPath());
    Files.deleteIfExists(tempDirectory);
  }

  @Test
  void shouldThrowExportExceptionWhenFileCannotBeWritten() throws IOException {
    // Given
    AtlasCsvMapper csvMapper = new AtlasCsvMapper(DummyCsvModel.class);
    DummyCsvModel model = new DummyCsvModel("value", LocalDate.of(2024, 1, 1));
    Path directoryPath = Files.createTempDirectory("csv-export-writer-error");
    File directoryAsFile = directoryPath.toFile();
    List<DummyCsvModel> csvData = List.of(model);
    ObjectWriter objectWriter = csvMapper.getObjectWriter();

    // When / Then
    assertThatThrownBy(() -> CsvExportWriter.writeToFile(directoryAsFile, csvData, objectWriter))
        .isInstanceOf(ExportException.class)
        .satisfies(exception -> assertThat(((ExportException) exception).getErrorResponse().getMessage())
            .contains(directoryAsFile.getName()));

    Files.deleteIfExists(directoryPath);
  }

  @Test
  void shouldWriteCsvWithLocalizedHeadersWhenUsingWriteCsvConfig() throws IOException {
    // Given
    StaticMessageSource messageSource = new StaticMessageSource();
    messageSource.addMessage("dateValue", Locale.GERMAN, "Datum");
    messageSource.addMessage("value", Locale.GERMAN, "Wert");
    DummyCsvModel model = new DummyCsvModel("stringValue", LocalDate.of(2020, 12, 31));
    Path tempDirectory = Files.createTempDirectory("csv-export-writer-localized");
    Path outputPath = tempDirectory.resolve("localized-export");

    CsvWriteConfig<DummyCsvModel> config = CsvWriteConfig.<DummyCsvModel>builder()
        .locale(Locale.GERMAN)
        .messageSource(messageSource)
        .elementClass(DummyCsvModel.class)
        .csvData(List.of(model))
        .filePath(outputPath)
        .build();

    // When
    File file = CsvExportWriter.writeCsv(config);

    // Then
    String result = Files.readString(file.toPath());
    String expectedCsv = """
        Datum;Wert
        "2020-12-31";stringValue
        """;
    assertThat(result).isEqualTo(CsvExportWriter.UTF_8_BYTE_ORDER_MARK + expectedCsv);

    Files.deleteIfExists(file.toPath());
    Files.deleteIfExists(tempDirectory);
  }
}
