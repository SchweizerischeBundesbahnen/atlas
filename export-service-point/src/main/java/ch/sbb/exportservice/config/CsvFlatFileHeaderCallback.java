package ch.sbb.exportservice.config;

import static ch.sbb.exportservice.config.SpringBatchConfig.CSV_HEADER;

import java.io.IOException;
import java.io.Writer;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class CsvFlatFileHeaderCallback implements FlatFileHeaderCallback {

  @Override
  public void writeHeader(Writer writer) throws IOException {
    for (int i = 0; i < CSV_HEADER.length; i++) {
      if (i != CSV_HEADER.length - 1) {
        writer.append(CSV_HEADER[i]).append(";");
      } else {
        writer.append(CSV_HEADER[i]);
      }
    }
  }
}
