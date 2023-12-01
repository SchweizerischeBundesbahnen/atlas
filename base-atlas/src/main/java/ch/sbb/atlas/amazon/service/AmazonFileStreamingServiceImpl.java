package ch.sbb.atlas.amazon.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RequiredArgsConstructor
public class AmazonFileStreamingServiceImpl implements AmazonFileStreamingService {

  private final AmazonService amazonService;
  private final FileService fileService;

  @Override
  public StreamingResponseBody streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream) {
    File file = amazonService.pullFile(amazonBucket, fileToStream);
    try(InputStream inputStream = new ByteArrayInputStream(fileService.gzipDecompress(file))){
      return fileService.toStreamingResponse(file, inputStream);
    } catch (IOException e) {
      throw new IllegalStateException("Could not stream the file", e);
    }
  }

  @Override
  public StreamingResponseBody streamFile(AmazonBucket amazonBucket, String fileToStream) {
    File file = amazonService.pullFile(amazonBucket, fileToStream);
    try {
      return fileService.toStreamingResponse(file, new FileInputStream(file));
    } catch (FileNotFoundException exception) {
      throw new IllegalStateException(exception);
    }
  }

}
