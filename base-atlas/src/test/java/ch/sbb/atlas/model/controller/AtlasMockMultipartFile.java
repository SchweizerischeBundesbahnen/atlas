package ch.sbb.atlas.model.controller;

import java.nio.charset.StandardCharsets;
import org.springframework.mock.web.MockMultipartFile;

public class AtlasMockMultipartFile extends MockMultipartFile {

  public AtlasMockMultipartFile(String name, String originalFilename, String contentType, String content) {
    super(name, originalFilename, contentType, content.getBytes(StandardCharsets.UTF_8));
  }
}
