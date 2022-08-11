package ch.sbb.atlas.amazon.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public interface AmazonService {

  URL putFile(File file) throws IOException;

  URL putZipFile(File file) throws IOException;

}
