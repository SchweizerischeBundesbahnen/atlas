package ch.sbb.line.directory.service.hearing;

import static ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants.MAX_DOCUMENTS;

import ch.sbb.line.directory.entity.StatementDocument;
import ch.sbb.line.directory.exception.PdfDocumentConstraintViolationException;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StatementDocumentFilesValidationService {

  private final TikaService tikaService;

  public StatementDocumentFilesValidationService(TikaService tikaService) {
    this.tikaService = tikaService;
  }

  public void validateAllFilessArePdfs(List<File> files) {
    List<String> documentFileNames = files.stream()
      .filter(file -> !tikaService.isFilePdf(file))
      .map(File::getName)
      .toList();
    if (!documentFileNames.isEmpty()) {
      String exceptionMessage = documentFileNames.stream()
        .map(documentName -> "The given document: " + documentName + " is not a valid PDF file.")
        .collect(Collectors.joining(File.separator));
      throw new PdfDocumentConstraintViolationException(exceptionMessage);
    }
  }

  public void validateMaxSizeOfFiles(List<File> files, Set<StatementDocument> alreadySavedDocuments, int maxDocumentsSize) {
    long sizeOfNewFiles = files.stream()
      .map(File::length)
      .mapToLong(Long::longValue)
      .sum();
    long sizeOfExistingFiles = alreadySavedDocuments.stream()
        .map(StatementDocument::getFileSize)
        .mapToLong(Long::longValue)
        .sum();
    long overallSizeOfFiles = sizeOfNewFiles + sizeOfExistingFiles;
    if (overallSizeOfFiles > maxDocumentsSize) {
      String exceptionMessage = "The combined size of all documents in bytes is: " + overallSizeOfFiles + " which exceeds the maximum allowed size of 20MB.";
      throw new PdfDocumentConstraintViolationException(exceptionMessage);
    }
  }

  public void validateMaxNumberOfFiles(Integer numberOfFiles) {
    if (numberOfFiles > MAX_DOCUMENTS) {
      String exceptionMessage = "Overall number of documents is: " + numberOfFiles + " which exceeds the number of allowed documents of 3.";
      throw new PdfDocumentConstraintViolationException(exceptionMessage);
    }
  }

  public void validateNoFileNameDuplicate(List<File> files, Set<StatementDocument> alreadySavedDocuments) {
    Set<String> savedFileNames = alreadySavedDocuments.stream().map(StatementDocument::getFileName).collect(Collectors.toSet());
    if (files.stream().map(File::getName).anyMatch(savedFileNames::contains)) {
      throw new PdfDocumentConstraintViolationException("FileName must be unique");
    }
  }
}
