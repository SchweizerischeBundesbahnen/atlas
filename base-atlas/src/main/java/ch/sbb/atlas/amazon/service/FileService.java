package ch.sbb.atlas.amazon.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface FileService {

  File zipFile(File file);

  String getDir();

  boolean clearDir();

  File getFileFromMultipart(MultipartFile multipartFile);

  StreamingResponseBody toStreamingResponse(File fileToCleanUp, InputStream inputStream);

  byte[] gzipDecompress(File file);

  byte[] gzipCompress(byte[] bytes) throws IOException;
}
