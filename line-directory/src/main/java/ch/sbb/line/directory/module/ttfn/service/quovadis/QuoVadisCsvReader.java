package ch.sbb.line.directory.module.ttfn.service.quovadis;

import ch.sbb.atlas.exception.CsvException;
import ch.sbb.atlas.imports.bulk.AtlasCsvReader;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.UtilityClass;

@UtilityClass
public class QuoVadisCsvReader {

  static List<QuoVadisDataRow> parseFile(File file) {
    List<QuoVadisDataRow> parsedLines = new ArrayList<>();
    try (MappingIterator<QuoVadisDataRow> objectMappingIterator = AtlasCsvReader.CSV_MAPPER
        .enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)
        .readerFor(QuoVadisDataRow.class)
        .with(AtlasCsvReader.CSV_SCHEMA)
        .readValues(file)) {
      objectMappingIterator.forEachRemaining(parsedLines::add);
    } catch (IOException e) {
      throw new CsvException(e);
    }
    return parsedLines;
  }

  @Data
  static class QuoVadisDataRow {

    @JsonProperty("EFA-Linienbezeichnung")
    private String number;

    @JsonProperty("Verkehrsmitteltextname")
    private String meanOfTransport;

    @JsonProperty("BuchUeberschrift")
    private String description;

    @JsonProperty("BuchUeberschrift-Zeile")
    private String rowCount;

    @JsonProperty("BuchUeberschrift-Richtung")
    private String direction;

    @JsonProperty("Unternehmer")
    private String businessOrganisation;
  }

}
