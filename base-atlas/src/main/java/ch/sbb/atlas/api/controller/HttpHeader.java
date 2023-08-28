package ch.sbb.atlas.api.controller;

import org.springframework.http.HttpHeaders;

public final class HttpHeader {

    private HttpHeader() {}

    public static HttpHeaders getHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/gzip");
        headers.add("Content-Disposition", "attachment;filename=" + fileName + ".json.gz");
        headers.add("Pragma", "no-cache");
        headers.add("Cache-Control", "no-cache");
        return headers;
    }
}
