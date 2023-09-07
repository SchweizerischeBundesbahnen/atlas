package ch.sbb.exportservice.writer;

import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.service.FileExportService;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public abstract class BaseCsvWriter<T> {

  abstract String[] getCsvHeader();

  private static final String DELIMITER = ";";

  @Autowired
  private FileExportService fileExportService;

  public FlatFileItemWriter<T> csvWriter(ExportType exportType, BatchExportFileName exportFileName) {
    WritableResource outputResource = new FileSystemResource(
        fileExportService.createFileNamePath(ExportExtensionFileType.CSV_EXTENSION,
            exportType, exportFileName));
    FlatFileItemWriter<T> writer = new FlatFileItemWriter<>();
    writer.setResource(outputResource);
    writer.setAppendAllowed(true);
    writer.setLineAggregator(new DelimitedLineAggregator<>() {
      {
        setDelimiter(DELIMITER);
        setFieldExtractor(new BeanWrapperFieldExtractor<>() {{
          setNames(getCsvHeader());
        }});
      }
    });
    writer.setHeaderCallback(new CsvFlatFileHeaderCallback(getCsvHeader()));
    writer.setEncoding(StandardCharsets.UTF_8.name());
    writer.close();
    return writer;
  }

}
