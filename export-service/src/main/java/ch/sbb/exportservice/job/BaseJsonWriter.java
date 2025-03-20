package ch.sbb.exportservice.job;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.core.io.FileSystemResource;

@RequiredArgsConstructor
public abstract class BaseJsonWriter<T> {

  private final FileService fileService;

  public JsonFileItemWriter<T> getWriter(ExportObjectV2 exportType, ExportTypeV2 exportFileName) {
    JacksonJsonObjectMarshaller<T> jacksonJsonObjectMarshaller = new JacksonJsonObjectMarshaller<>();
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    jacksonJsonObjectMarshaller.setObjectMapper(objectMapper);
    JsonFileItemWriter<T> writer = new JsonFileItemWriter<>(
        new FileSystemResource(getFilePath(exportType, exportFileName)),
        jacksonJsonObjectMarshaller);
    writer.setEncoding(StandardCharsets.UTF_8.name());
    writer.close();
    return writer;
  }

  private String getFilePath(ExportObjectV2 exportType, ExportTypeV2 exportFileName) {
    return ExportFilePathV2.getV2Builder(exportType, exportFileName)
        .systemDir(fileService.getDir())
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .build()
        .actualDateFilePath();
  }

}
