package ch.sbb.atlas.export;

import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.BaseExportService.AtlasCsvMapper;
import ch.sbb.atlas.export.exception.ExportException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class ExportWriter {

  public <T> File writeToFile(String pathname, Iterable<T> csvData, Class<T> clazz) {
    ObjectWriter objectWriter = new AtlasCsvMapper(clazz).getObjectWriter();
    return writeToFile(pathname, csvData, objectWriter);
  }

  public File writeToFile(String pathname, Iterable<?> csvData, ObjectWriter objectWriter) {
    String currentDateTime = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern(AtlasApiConstants.DATE_TIME_FOR_FILE_FORMAT_PATTERN));
    File file = new File(pathname + "_" + currentDateTime + ".csv");

    return writeToFile(file, csvData, objectWriter);
  }

  public File writeToFile(File file, Iterable<?> csvData, ObjectWriter objectWriter) {
    try (BufferedWriter bufferedWriter = new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
        SequenceWriter sequenceWriter = objectWriter.writeValues(bufferedWriter)) {
      bufferedWriter.write(BaseExportService.UTF_8_BYTE_ORDER_MARK);
      sequenceWriter.writeAll(csvData);
      return file;
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new ExportException(file, e);
    }
  }
}
