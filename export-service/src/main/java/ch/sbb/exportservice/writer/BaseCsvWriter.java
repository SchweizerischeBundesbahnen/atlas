package ch.sbb.exportservice.writer;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePath;
import ch.sbb.exportservice.model.ExportObject;
import ch.sbb.exportservice.model.ExportType;
import java.nio.charset.StandardCharsets;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseCsvWriter<T> {

  abstract String[] getCsvHeader();

  private static final String DELIMITER = ";";

  @Autowired
  private FileService fileService;

  public FlatFileItemWriter<T> csvWriter(ExportObject exportType, ExportType exportFileName) {
    FlatFileItemWriter<T> writer = new FlatFileItemWriter<>();
    writer.setResource(new FileSystemResource(getFilePath(exportType, exportFileName)));
    writer.setAppendAllowed(true);
    writer.setLineAggregator(getLineAggregator());
    writer.setHeaderCallback(new CsvFlatFileHeaderCallback(getCsvHeader()));
    writer.setEncoding(StandardCharsets.UTF_8.name());
    writer.close();
    return writer;
  }

  private String getFilePath(ExportObject exportType, ExportType exportFileName) {
    return ExportFilePath.getV2Builder(exportType, exportFileName)
        .systemDir(fileService.getDir())
        .extension(ExportExtensionFileType.CSV_EXTENSION.getExtension())
        .build()
        .actualDateFilePath();
  }

  private DelimitedLineAggregator<T> getLineAggregator() {
    DelimitedLineAggregator<T> lineAggregator = new DelimitedLineAggregator<>();
    lineAggregator.setDelimiter(DELIMITER);
    lineAggregator.setFieldExtractor(getFieldExtractor());
    return lineAggregator;
  }

  private AtlasWrapperFieldExtractor<T> getFieldExtractor() {
    return new AtlasWrapperFieldExtractor<>(getCsvHeader());
  }

}
