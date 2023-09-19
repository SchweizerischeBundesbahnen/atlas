package ch.sbb.line.directory.service;

import static ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants.MAX_DOCUMENTS_SIZE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.model.controller.IntegrationTest;
import ch.sbb.line.directory.entity.StatementDocument;
import ch.sbb.line.directory.exception.PdfDocumentConstraintViolationException;
import ch.sbb.line.directory.helper.PdfFiles;
import ch.sbb.line.directory.service.hearing.StatementDocumentFilesValidationService;
import ch.sbb.line.directory.service.hearing.TikaService;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

@IntegrationTest
 class DocumentsValidationServiceTest {

  private final TikaService tikaService = new TikaService();
  private final StatementDocumentFilesValidationService documentsValidationService = new StatementDocumentFilesValidationService(tikaService);
  private final List<File> files = PdfFiles.FILES;

  private Set<StatementDocument> statementDocumentsWithFilesSize10MB() {
    StatementDocument statementDocument =  new StatementDocument();
    statementDocument.setFileSize(10485760L);
    Set<StatementDocument> statementDocuments = new HashSet<>();
    statementDocuments.add(statementDocument);
    return statementDocuments;
  }

  private Set<StatementDocument> statementDocumentsWithFilesSizeAlmost20MB() {
    StatementDocument statementDocument =  new StatementDocument();
    statementDocument.setFileSize(10485000L);
    Set<StatementDocument> statementDocuments = statementDocumentsWithFilesSize10MB();
    statementDocuments.add(statementDocument);
    return statementDocuments;
  }

  private Set<StatementDocument> statementDocumentsWithoutDuplicatedFileNames() {
    StatementDocument statementDocument =  new StatementDocument();
    statementDocument.setFileName("noduplicatedname.pdf");
    Set<StatementDocument> statementDocuments = new HashSet<>();
    statementDocuments.add(statementDocument);
    return statementDocuments;
  }

  private Set<StatementDocument> statementDocumentsWithDuplicatedFileNames() {
    StatementDocument statementDocument =  new StatementDocument();
    statementDocument.setFileName("dummy.pdf");
    Set<StatementDocument> statementDocuments = statementDocumentsWithoutDuplicatedFileNames();
    statementDocuments.add(statementDocument);
    return statementDocuments;
  }

  @Test
   void givenListOfFilesValidateTheyArePdf() {
    List<File> pdfFiles = new ArrayList<>();
    pdfFiles.add(files.get(0));
    pdfFiles.add(files.get(1));
    pdfFiles.add(files.get(2));
    pdfFiles.add(files.get(3));
    assertDoesNotThrow(() -> documentsValidationService.validateAllFilessArePdfs(pdfFiles));
  }

  @Test
   void givenListOfFilesValidateTheyAreNotPdf() {
    Executable executable = () -> documentsValidationService.validateAllFilessArePdfs(files);
    Exception exception = assertThrows(PdfDocumentConstraintViolationException.class, executable);
    assertEquals(exception.getMessage(), "The given document: test.txt is not a valid PDF file." + (File.separator) + "The given document: test1.txt is not a valid PDF file.");
  }

  @Test
   void givenListOfFilesValidateNumberOfFilesIsGreaterThanAllowed() {
    Executable executable = () -> documentsValidationService.validateMaxNumberOfFiles(files.size());
    Exception exception = assertThrows(PdfDocumentConstraintViolationException.class, executable);
    assertEquals(exception.getMessage(), "Overall number of documents is: 6 which exceeds the number of allowed documents of 3.");
  }

  @Test
   void givenListOfFilesAndStatementDocumentsValidateSizeOfFilesIsNotGreaterThanAllowed() {
    assertDoesNotThrow(() -> documentsValidationService.validateMaxSizeOfFiles(files, statementDocumentsWithFilesSize10MB(), MAX_DOCUMENTS_SIZE));
  }

  @Test
   void givenListOfFilesAndStatementDocumentsValidateSizeOfFilesGreaterThanAllowed() {
    Executable executable = () -> documentsValidationService.validateMaxSizeOfFiles(files, statementDocumentsWithFilesSizeAlmost20MB(), MAX_DOCUMENTS_SIZE);
    Exception exception = assertThrows(PdfDocumentConstraintViolationException.class, executable);
    assertEquals(exception.getMessage(), "The combined size of all documents in bytes is: 21023860 which exceeds the maximum allowed size of 20MB.");
  }

  @Test
   void givenListOfFilesAndStatementDocumentsValidateThereAreNoDuplicateNames() {
    assertDoesNotThrow(() -> documentsValidationService.validateNoFileNameDuplicate(files, statementDocumentsWithoutDuplicatedFileNames()));
  }

  @Test
   void givenListOfFilesAndStatementDocumentsValidateThereAreDuplicateNames() {
    Executable executable = () -> documentsValidationService.validateNoFileNameDuplicate(files, statementDocumentsWithDuplicatedFileNames());
    Exception exception = assertThrows(PdfDocumentConstraintViolationException.class, executable);
    assertEquals(exception.getMessage(), "FileName must be unique");
  }

}
