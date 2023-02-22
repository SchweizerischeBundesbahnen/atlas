package ch.sbb.atlas.amazon.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface AmazonService {

  URL putFile(File file, String dir) throws IOException;

  URL putZipFile(File file, String dir) throws IOException;

  File pullFile(String filePath) throws IOException;

  List<String> getS3ObjectKeysFromPrefix(String dirPath, String prefix);
}
