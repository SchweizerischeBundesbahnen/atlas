package ch.sbb.atlas.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

class GzipFileDownloadHttpHeaderTest {

  @Test
  void shouldGetHttpHeader() {
    HttpHeaders httpHeaders = GzipFileDownloadHttpHeader.getHeaders("filename");

    assertEquals("application/gzip", Objects.requireNonNull(httpHeaders.getContentType()).toString());
    assertEquals("attachment; filename=\"filename.json.gz\"", httpHeaders.getContentDisposition().toString());
    assertEquals("no-cache", httpHeaders.getPragma());
    assertEquals("no-cache", httpHeaders.getCacheControl());
  }

}
