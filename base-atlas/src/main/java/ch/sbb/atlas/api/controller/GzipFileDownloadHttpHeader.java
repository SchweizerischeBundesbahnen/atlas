package ch.sbb.atlas.api.controller;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

@UtilityClass
public final class GzipFileDownloadHttpHeader {

    public static HttpHeaders getHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/gzip");
        headers.add("Content-Disposition", "attachment;filename=" + fileName + ".json.gz");
        headers.add("Pragma", "no-cache");
        headers.add("Cache-Control", "no-cache");
        return headers;
    }
}
