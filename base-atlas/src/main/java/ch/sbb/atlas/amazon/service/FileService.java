package ch.sbb.atlas.amazon.service;

import ch.sbb.atlas.export.enumeration.ExportFileName;
import ch.sbb.atlas.export.enumeration.ExportTypeBase;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;

public interface FileService {

  File zipFile(File file);

  String getDir();

  boolean clearDir();

  File getFileFromMultipart(MultipartFile multipartFile);

  StreamingResponseBody streamingJsonFile(ExportTypeBase exportType, ExportFileName exportFileName,
      AmazonService amazonService, String fileName);

  StreamingResponseBody streamingGzipFile(ExportTypeBase exportType, ExportFileName exportFileName,
      AmazonService amazonService, String fileName);

}
