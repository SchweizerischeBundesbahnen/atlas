package ch.sbb.line.directory.service;

import static ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants.MAX_DOCUMENTS_SIZE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.helper.PdfFiles;
import ch.sbb.line.directory.exception.PdfDocumentConstraintViolationException;
import ch.sbb.line.directory.service.hearing.StatementDocumentFilesValidationService;
import ch.sbb.line.directory.service.hearing.TikaService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

@IntegrationTest
public class DocumentsValidationServiceTest {

  private final TikaService tikaService = new TikaService();
  private final StatementDocumentFilesValidationService documentsValidationService = new StatementDocumentFilesValidationService(tikaService);
  private final List<File> files = PdfFiles.FILES;

  @Test
  public void givenListOfFilesValidateTheyArePdf() {
    List<File> pdfFiles = new ArrayList<>();
    pdfFiles.add(files.get(0));
    pdfFiles.add(files.get(1));
    pdfFiles.add(files.get(2));
    pdfFiles.add(files.get(3));
    assertDoesNotThrow(() -> documentsValidationService.validateAllFilessArePdfs(pdfFiles));
  }

  @Test
  public void givenListOfFilesValidateTheyAreNotPdf() {
    Executable executable = () -> documentsValidationService.validateAllFilessArePdfs(files);
    Exception exception = assertThrows(PdfDocumentConstraintViolationException.class, executable);
    assertEquals(exception.getMessage(), "The given document: test.txt is not a valid PDF file." + (File.separator) + "The given document: test1.txt is not a valid PDF file.");
  }

  @Test
  public void givenListOfFilesValidateNumberOfFilesIsGreaterThanAllowed() {
    Executable executable = () -> documentsValidationService.validateMaxNumberOfFiles(files.size());
    Exception exception = assertThrows(PdfDocumentConstraintViolationException.class, executable);
    assertEquals(exception.getMessage(), "Overall number of documents is: 6 which exceeds the number of allowed documents of 3.");
  }

  @Test
  public void givenListOfFilesValidateSizeOfFilesIsNotGreaterThanAllowed() {
    assertDoesNotThrow(() -> documentsValidationService.validateMaxSizeOfFiles(files, MAX_DOCUMENTS_SIZE));
  }

  @Test
  public void givenListOfFilesValidateSizeOfFilesGreaterThanAllowed() {
    Executable executable = () -> documentsValidationService.validateMaxSizeOfFiles(files, 1);
    Exception exception = assertThrows(PdfDocumentConstraintViolationException.class, executable);
    assertEquals(exception.getMessage(), "The combined size of all documents in bytes is: 53100 which exceeds the maximum allowed size of 20MB.");
  }

}
