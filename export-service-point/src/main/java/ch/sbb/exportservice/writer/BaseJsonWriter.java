package ch.sbb.exportservice.writer;

import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.service.FileExportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.StandardCharsets;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseJsonWriter<T> {

  @Autowired
  private FileExportService fileExportService;

  public JsonFileItemWriter<T> getWriter(ExportTypeBase exportType, ExportFileName exportFileName) {
    JacksonJsonObjectMarshaller<T> jacksonJsonObjectMarshaller = new JacksonJsonObjectMarshaller<>();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    jacksonJsonObjectMarshaller.setObjectMapper(objectMapper);
    FileSystemResource fileSystemResource =
        new FileSystemResource(fileExportService.createExportFilePath(exportType, exportFileName,
            ExportExtensionFileType.JSON_EXTENSION).actualDateFilePath());
    JsonFileItemWriter<T> writer = new JsonFileItemWriter<>(
        fileSystemResource,
        jacksonJsonObjectMarshaller);
    writer.setEncoding(StandardCharsets.UTF_8.name());
    writer.close();
    return writer;
  }

}
