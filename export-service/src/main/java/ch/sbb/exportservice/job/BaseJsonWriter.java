package ch.sbb.exportservice.job;

import ch.sbb.atlas.s3.service.FileService;
import ch.sbb.exportservice.model.ExportExtensionFileType;
import ch.sbb.exportservice.model.ExportFilePathV2;
import ch.sbb.exportservice.model.ExportObjectV2;
import ch.sbb.exportservice.model.ExportTypeV2;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.infrastructure.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.infrastructure.item.json.JsonFileItemWriter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.WritableResource;
import tools.jackson.databind.json.JsonMapper;

@RequiredArgsConstructor
public abstract class BaseJsonWriter<T> {

  private final FileService fileService;

  public JsonFileItemWriter<T> getWriter(ExportObjectV2 exportType, ExportTypeV2 exportFileName) {
    return getWriter(new FileSystemResource(getFilePath(exportType, exportFileName)));
  }

  public JsonFileItemWriter<T> getWriter(WritableResource writableResource) {
    JsonFileItemWriter<T> writer = new JsonFileItemWriter<>(writableResource, createJsonMarshaller());
    writer.setEncoding(StandardCharsets.UTF_8.name());
    writer.close();
    return writer;
  }

  public JacksonJsonObjectMarshaller<T> createJsonMarshaller() {
    JacksonJsonObjectMarshaller<T> marshaller = new JacksonJsonObjectMarshaller<>();
    marshaller.setJsonMapper(JsonMapper.builder().build());
    return marshaller;
  }

  private String getFilePath(ExportObjectV2 exportType, ExportTypeV2 exportFileName) {
    return ExportFilePathV2.getV2Builder(exportType, exportFileName)
        .systemDir(fileService.getDir())
        .extension(ExportExtensionFileType.JSON_EXTENSION.getExtension())
        .build()
        .actualDateFilePath();
  }

}
