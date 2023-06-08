package ch.sbb.exportservice.service;

import ch.sbb.atlas.amazon.exception.FileException;
import ch.sbb.atlas.amazon.service.AmazonBucket;
import ch.sbb.atlas.amazon.service.AmazonService;
import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.AtlasApiConstants;
import ch.sbb.atlas.export.ExportType;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileExportService {

  private static final String EXPORT_DIR = "service_point";
  private final FileService fileService;
  private final AmazonService amazonService;

  public List<URL> exportFiles(List<File> files, ExportServicePointDirectory directory) {
    List<URL> urls = new ArrayList<>();
    String pathDirectory = EXPORT_DIR + "/" + directory.getSubDir();
    files.forEach(file -> {
      try {
        urls.add(amazonService.putFile(AmazonBucket.EXPORT, file, pathDirectory));
      } catch (IOException e) {
        throw new FileException("Error uploading file: " + file.getName() + " to bucket: " + AmazonBucket.EXPORT, e);

      }
    });
    return urls;
  }

  protected File createFile(ExportType exportType, String fileName) {
    String dir = fileService.getDir();
    String actualDate = LocalDate.now()
        .format(DateTimeFormatter.ofPattern(
            AtlasApiConstants.DATE_FORMAT_PATTERN));
    return new File(dir + exportType.getFilePrefix() + fileName + actualDate + ".csv");
  }
}
