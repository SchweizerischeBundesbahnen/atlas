package ch.sbb.atlas.amazon.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileService {

  public static final int BUFFER_SIZE = 4096;
  public static final int ZERO = 0;
  public static final String ZIP = ".zip";
  @Getter
  @Value("${file.dir}")
  private String dir;

  public File zipFile(File file, String filename) {
    File zipFile = new File(getDir() + filename + ZIP);

    try (ZipOutputStream zipStream = new ZipOutputStream(new FileOutputStream(zipFile));
        InputStream inputStream = new FileInputStream(file)) {

      ZipEntry entry = new ZipEntry(filename);
      zipStream.putNextEntry(entry);

      copyStream(inputStream, zipStream);
      zipStream.flush();

    } catch (Exception e) {
      log.error("Error during write ZipFile", e);
    }
    if (!zipFile.canRead()) {
      zipFile.setReadable(true);
    }
    return zipFile;
  }

  private void copyStream(InputStream in, OutputStream out) throws IOException {
    byte[] buf = new byte[BUFFER_SIZE];
    int len;
    while ((len = in.read(buf)) >= ZERO) {
      out.write(buf, ZERO, len);
    }
    out.flush();
  }

  public ByteArrayOutputStream getOutputStream(InputStream isObjectContent) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      int len;
      byte[] buffer = new byte[BUFFER_SIZE];
      while ((len = isObjectContent.read(buffer, ZERO, buffer.length)) != -1) {
        outputStream.write(buffer, ZERO, len);
      }
      return outputStream;
    } catch (IOException ioException) {
      log.error("IOException: " + ioException.getMessage());
    } catch (AmazonServiceException serviceException) {
      log.error("AmazonServiceException Message:    " + serviceException.getMessage());
      throw serviceException;
    } catch (AmazonClientException clientException) {
      log.error("AmazonClientException Message: " + clientException.getMessage());
      throw clientException;
    }
    return null;
  }

  public MediaType contentType(String filename) {
    String[] fileArrSplit = filename.split("\\.");
    String fileExtension = fileArrSplit[fileArrSplit.length - 1];
    return switch (fileExtension) {
      case "txt" -> MediaType.TEXT_PLAIN;
      case "png" -> MediaType.IMAGE_PNG;
      case "jpg" -> MediaType.IMAGE_JPEG;
      default -> MediaType.APPLICATION_OCTET_STREAM;
    };
  }

  public String getDir() {
    if (this.dir == null) {
      return "";
    }
    return this.dir;
  }

}
