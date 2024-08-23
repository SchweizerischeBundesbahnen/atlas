package ch.sbb.importservice.service.bulk;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.exception.CsvException;
import ch.sbb.importservice.service.ExcelToCsvConverter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkImportFileValidationService {

  private static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";
  private static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  private static final String CSV_CONTENT_TYPE = "text/csv";

  private static final List<String> SUPPORTED_CONTENT_TYPES = List.of(CSV_CONTENT_TYPE, XLS_CONTENT_TYPE, XLSX_CONTENT_TYPE);
  private static final List<String> SUPPORTED_EXCEL_CONTENT_TYPES = List.of(XLS_CONTENT_TYPE, XLSX_CONTENT_TYPE);

  private final FileService fileService;

  public File validateFileAndPrepareFile(MultipartFile multipartFile) {
    valdiateSupportedContentType(multipartFile.getContentType());

    File csvFile = getMultipartAsCsvFile(multipartFile);
    validateFileHeader(csvFile);

    return csvFile;
  }

  private static void validateFileHeader(File file) {
    try (Scanner scanner = new Scanner(file)) {
      if (scanner.hasNext()) {
        log.info("Header multi {}", scanner.nextLine());
      }
    } catch (IOException ex) {
      throw new CsvException(ex);
    }
  }

  private File getMultipartAsCsvFile(MultipartFile multipartFile) {
    File file = fileService.getFileFromMultipart(multipartFile);
    if (SUPPORTED_EXCEL_CONTENT_TYPES.contains(multipartFile.getContentType())) {
      file = ExcelToCsvConverter.convertToCsv(file);
    }
    return file;
  }

  private static void valdiateSupportedContentType(String contentType) {
    if (!SUPPORTED_CONTENT_TYPES.contains(contentType)) {
      log.error("Unsupported content type {}", contentType);
    }
  }

}
