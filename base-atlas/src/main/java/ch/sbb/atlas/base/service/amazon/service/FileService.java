package ch.sbb.atlas.base.service.amazon.service;

import java.io.File;

public interface FileService {

  File zipFile(File file);

  String getDir();

  boolean clearDir();
}
