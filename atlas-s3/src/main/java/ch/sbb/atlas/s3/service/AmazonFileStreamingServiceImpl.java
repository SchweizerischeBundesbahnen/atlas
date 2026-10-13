package ch.sbb.atlas.s3.service;

import ch.sbb.atlas.s3.config.AmazonBucket;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public class AmazonFileStreamingServiceImpl implements AmazonFileStreamingService {

  static final long MAX_UNCOMPRESSED_ZIP_ENTRY_BYTES = 20L * 1024 * 1024 * 1024;

  private final AmazonService amazonService;
  private final FileService fileService;
  private final long maxUncompressedBytes;

  public AmazonFileStreamingServiceImpl(AmazonService amazonService, FileService fileService) {
    this(amazonService, fileService, MAX_UNCOMPRESSED_ZIP_ENTRY_BYTES);
  }

  AmazonFileStreamingServiceImpl(AmazonService amazonService, FileService fileService, long maxUncompressedBytes) {
    this.amazonService = amazonService;
    this.fileService = fileService;
    this.maxUncompressedBytes = maxUncompressedBytes;
  }

  @Override
  public StreamedFile streamFileAndDecompress(AmazonBucket amazonBucket, String fileToStream) {
    try (InputStream s3Object = amazonService.pullS3Object(amazonBucket, fileToStream)) {
      byte[] decompressed = fileService.gzipDecompress(s3Object);
      InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(decompressed));
      return new StreamedFile(resource, ContentLength.of(decompressed.length));
    } catch (IOException e) {
      throw new IllegalStateException("Could not stream the file", e);
    }
  }

  @Override
  public StreamedFile streamFile(AmazonBucket amazonBucket, String fileToStream) {
    InputStreamResource resource = amazonService.pullFileAsStream(amazonBucket, fileToStream);
    ContentLength contentLength = amazonService.getObjectContentLength(amazonBucket, fileToStream);
    return new StreamedFile(resource, contentLength);
  }

  @Override
  public StreamingResponseBody streamZipEntry(AmazonBucket amazonBucket, String fileToStream) {
    InputStream s3Object = amazonService.pullS3Object(amazonBucket, fileToStream);
    return outputStream -> {
      try (InputStream inputStream = s3Object; ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        if (zipEntry == null) {
          throw new IllegalStateException("Zip file " + fileToStream + " does not contain any entry");
        }
        // Zip bomb protection: stream in chunks and abort once the uncompressed size exceeds the limit
        byte[] buffer = new byte[8192];
        long total = 0;
        int read;
        while ((read = zipInputStream.read(buffer)) != -1) {
          total += read;
          if (total > maxUncompressedBytes) {
            throw new IllegalStateException(
                "Zip entry of " + fileToStream + " exceeds the maximum uncompressed size of " + maxUncompressedBytes + " bytes");
          }
          outputStream.write(buffer, 0, read);
        }
      }
    };
  }

}
