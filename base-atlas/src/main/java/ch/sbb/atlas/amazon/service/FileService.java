package ch.sbb.atlas.amazon.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public interface FileService {

  File zipFile(File file);

  String getDir();

  boolean clearDir();

  File getFileFromMultipart(MultipartFile multipartFile);

  StreamingResponseBody writeOutputStream(File fileToCleanUp, InputStream inputStream);

  byte[] decompressGzipToBytes(Path source) throws IOException;
}
