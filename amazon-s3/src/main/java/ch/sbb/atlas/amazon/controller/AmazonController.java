package ch.sbb.atlas.amazon.controller;

import java.io.File;
import java.net.URL;
import org.springframework.http.ResponseEntity;

public interface AmazonController {

  ResponseEntity<URL> putFile(File file);

  ResponseEntity<URL> putZipFile(File file);

}
