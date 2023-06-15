package ch.sbb.exportservice.writer;

import ch.sbb.atlas.api.servicepoint.ServicePointVersionModel;
import ch.sbb.exportservice.model.ExportFileType;
import ch.sbb.exportservice.model.ServicePointExportType;
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
public class JsonServicePointWriter {

  @Autowired
  private FileExportService fileExportService;

  public JsonFileItemWriter<ServicePointVersionModel> getWriter(ServicePointExportType exportType) {
    JacksonJsonObjectMarshaller<ServicePointVersionModel> jacksonJsonObjectMarshaller = new JacksonJsonObjectMarshaller<>();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    jacksonJsonObjectMarshaller.setObjectMapper(objectMapper);
    FileSystemResource fileSystemResource =
        new FileSystemResource(fileExportService.createFileNamePath(ExportFileType.JSON_EXTENSION,
            exportType));
    JsonFileItemWriter<ServicePointVersionModel> writer = new JsonFileItemWriter<>(
        fileSystemResource,
        jacksonJsonObjectMarshaller);
    writer.setEncoding(StandardCharsets.ISO_8859_1.name());
    return writer;
  }

}
