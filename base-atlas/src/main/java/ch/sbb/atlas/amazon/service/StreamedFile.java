package ch.sbb.atlas.amazon.service;

import java.util.Objects;
import org.springframework.core.io.InputStreamResource;

/**
 * Carries a streamed file together with its known {@link ContentLength} so that callers can set an explicit
 * {@code Content-Length} header. This avoids falling back to {@code Transfer-Encoding: chunked}, which is a
 * connection-specific (hop-by-hop) header that is forbidden in HTTP/2 (RFC 9113 §8.2.2) and must be stripped by
 * intermediaries (RFC 9110 §7.6.1).
 */
public record StreamedFile(InputStreamResource resource, ContentLength contentLength) {

  public StreamedFile {
    Objects.requireNonNull(resource, "resource must not be null");
    Objects.requireNonNull(contentLength, "contentLength must not be null");
  }

}
