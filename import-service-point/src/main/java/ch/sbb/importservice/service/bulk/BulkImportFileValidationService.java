package ch.sbb.importservice.service.bulk;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.imports.bulk.AtlasCsvReader;
import ch.sbb.importservice.exception.ContentTypeFileValidationException;
import ch.sbb.importservice.exception.FileHeaderValidationException;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.service.ExcelToCsvConverter;
import ch.sbb.importservice.service.bulk.reader.BulkImportReaders;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
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
  private final BulkImportReaders bulkImportReaders;

  public File validateFileAndPrepareFile(MultipartFile multipartFile, BulkImportConfig bulkImportConfig) {
    validateSupportedContentType(multipartFile.getContentType());

    File csvFile = getMultipartAsCsvFile(multipartFile);
    validateFileHeader(csvFile, bulkImportConfig);

    return csvFile;
  }

  private static void validateSupportedContentType(String contentType) {
    if (!SUPPORTED_CONTENT_TYPES.contains(contentType)) {
      throw new ContentTypeFileValidationException(contentType);
    }
  }

  private File getMultipartAsCsvFile(MultipartFile multipartFile) {
    File file = fileService.getFileFromMultipart(multipartFile);
    if (SUPPORTED_EXCEL_CONTENT_TYPES.contains(multipartFile.getContentType())) {
      file = ExcelToCsvConverter.convertToCsv(file);
    }
    return file;
  }

  private void validateFileHeader(File file, BulkImportConfig bulkImportConfig) {
    String fileHeader = getFileHeader(file);
    String expectedFileHeader = getExpectedFileHeader(bulkImportConfig);
    if (!Objects.equals(fileHeader, expectedFileHeader)) {
      throw new FileHeaderValidationException();
    }
  }

  private String getExpectedFileHeader(BulkImportConfig bulkImportConfig) {
    Class<?> csvModelClass = bulkImportReaders.getReaderFunction(bulkImportConfig).getCsvModelClass();
    JsonPropertyOrder jsonPropertyOrder = csvModelClass.getAnnotation(JsonPropertyOrder.class);
    return String.join(String.valueOf(AtlasCsvReader.CSV_COLUMN_SEPARATOR), jsonPropertyOrder.value());
  }

  private static String getFileHeader(File file) {
    try (Scanner scanner = new Scanner(file)) {
      if (scanner.hasNext()) {
        return scanner.nextLine();
      }
      return null;
    } catch (IOException ex) {
      return null;
    }
  }

}
