package ch.sbb.exportservice.writer;

import ch.sbb.exportservice.model.BatchExportFileName;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportType;
import ch.sbb.exportservice.service.FileExportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public abstract class BaseJsonWriter<T> {

  @Autowired
  private FileExportService fileExportService;

  public JsonFileItemWriter<T> getWriter(ExportType exportType, BatchExportFileName exportFileName) {
    JacksonJsonObjectMarshaller<T> jacksonJsonObjectMarshaller = new JacksonJsonObjectMarshaller<>();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    jacksonJsonObjectMarshaller.setObjectMapper(objectMapper);
    FileSystemResource fileSystemResource =
        new FileSystemResource(fileExportService.createFileNamePath(ExportExtensionFileType.JSON_EXTENSION,
            exportType, exportFileName));
    JsonFileItemWriter<T> writer = new JsonFileItemWriter<>(
        fileSystemResource,
        jacksonJsonObjectMarshaller);
    writer.setEncoding(StandardCharsets.UTF_8.name());
    return writer;
  }

}
