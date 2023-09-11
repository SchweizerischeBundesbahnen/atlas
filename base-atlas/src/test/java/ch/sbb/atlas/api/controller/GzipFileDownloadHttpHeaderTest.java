package ch.sbb.atlas.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GzipFileDownloadHttpHeaderTest {

    @Test
    void shouldGetHttpHeader() {
        HttpHeaders httpHeaders = GzipFileDownloadHttpHeader.getHeaders("filename");

        assertEquals("application/gzip", Objects.requireNonNull(httpHeaders.getContentType()).toString());
        assertEquals("attachment; filename=\"filename.json.gz\"", httpHeaders.getContentDisposition().toString());
        assertEquals("no-cache", httpHeaders.getPragma());
        assertEquals("no-cache", httpHeaders.getCacheControl());
    }

}
