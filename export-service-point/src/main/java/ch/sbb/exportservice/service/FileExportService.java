package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileExportService {

  private static final String EXPORT_DIR = "service_point";
  private final AmazonService amazonService;

  public List<URL> exportFiles(List<File> files, ExportServicePointDirectory directory) {
    List<URL> urls = new ArrayList<>();
    String pathDirectory = EXPORT_DIR + "/" + directory.getSubDir();
    files.forEach(file -> {
      try {
        urls.add(amazonService.putGzipFile(AmazonBucket.EXPORT, file, pathDirectory));
      } catch (IOException e) {
        throw new FileException("Error uploading file: " + file.getName() + " to bucket: " + AmazonBucket.EXPORT, e);

      }
    });
    return urls;
  }

}
