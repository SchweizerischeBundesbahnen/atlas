package ch.sbb.importservice.service.bulk;

import static ch.sbb.importservice.service.bulk.reader.BulkImportCsvReader.getFileHeader;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.imports.bulk.AtlasCsvReader;
import ch.sbb.importservice.exception.ContentTypeFileValidationException;
import ch.sbb.importservice.exception.FileHeaderValidationException;
import ch.sbb.importservice.model.BulkImportConfig;
import ch.sbb.importservice.service.ExcelToCsvConverter;
import ch.sbb.importservice.service.bulk.reader.BulkImportReaders;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.io.File;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkImportFileValidationService {

  public static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";
  public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
  public static final String CSV_CONTENT_TYPE = "text/csv";

  private static final List<String> SUPPORTED_CONTENT_TYPES = List.of(CSV_CONTENT_TYPE, XLS_CONTENT_TYPE, XLSX_CONTENT_TYPE);
  private static final List<String> SUPPORTED_EXCEL_CONTENT_TYPES = List.of(XLS_CONTENT_TYPE, XLSX_CONTENT_TYPE);

  private final FileService fileService;
  private final BulkImportReaders bulkImportReaders;
  private final ExcelToCsvConverter excelToCsvConverter;

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
      file = excelToCsvConverter.convertToCsv(file);
    }
    return file;
  }

  public void validateFileHeader(File file, BulkImportConfig bulkImportConfig) {
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

}
