package ch.sbb.exportservice.writer;

import static ch.sbb.atlas.export.CsvExportWriter.UTF_8_BYTE_ORDER_MARK;

import java.io.IOException;
import java.io.Writer;
import org.springframework.batch.item.file.FlatFileHeaderCallback;

public class CsvFlatFileHeaderCallback implements FlatFileHeaderCallback {

  private final String[] csvHeader;

  public CsvFlatFileHeaderCallback(String[] csvHeader) {
    this.csvHeader = csvHeader;
  }

  @Override
  public void writeHeader(Writer writer) throws IOException {
    writer.append(UTF_8_BYTE_ORDER_MARK);

    for (int i = 0; i < this.csvHeader.length; i++) {
      if (i != this.csvHeader.length - 1) {
        writer.append(this.csvHeader[i]).append(";");
      } else {
        writer.append(this.csvHeader[i]);
      }
    }
  }
}
