package ch.sbb.atlas.amazon.service;

import java.io.File;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

  File zipFile(File file);

  String getDir();

  boolean clearDir();

  File getFileFromMultipart(MultipartFile multipartFile);
}
