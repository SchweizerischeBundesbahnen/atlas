package ch.sbb.atlas.amazon.service;

import org.springframework.core.io.InputStreamResource;

public record StreamedFile(InputStreamResource resource, long contentLength) {

}
