package ch.sbb.exportservice.config;

import java.io.IOException;
import java.io.Writer;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class CsvFlatFileHeaderCallback implements FlatFileHeaderCallback {

  private String[] csvHeader;

  public CsvFlatFileHeaderCallback(String[] csvHeader) {
    this.csvHeader = csvHeader;
  }

  @Override
  public void writeHeader(Writer writer) throws IOException {
    for (int i = 0; i < this.csvHeader.length; i++) {
      if (i != this.csvHeader.length - 1) {
        writer.append(this.csvHeader[i]).append(";");
      } else {
        writer.append(this.csvHeader[i]);
      }
    }
  }
}
