package ch.sbb.atlas.amazon.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RequiredArgsConstructor
public class BaseAmazonFileStreamingService {

  protected final AmazonService amazonService;
  protected final FileService fileService;

  protected StreamingResponseBody streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream) {
    File file = amazonService.pullFile(amazonBucket, fileToStream);
    InputStream inputStream = new ByteArrayInputStream(fileService.decompressGzipToBytes(file));
    return fileService.toStreamingResponse(file, inputStream);
  }

  protected StreamingResponseBody streamFile(AmazonBucket amazonBucket, String fileToStream) {
    File file = amazonService.pullFile(amazonBucket, fileToStream);
    try {
      return fileService.toStreamingResponse(file, new FileInputStream(file));
    } catch (FileNotFoundException exception) {
      throw new IllegalStateException(exception);
    }
  }
}
