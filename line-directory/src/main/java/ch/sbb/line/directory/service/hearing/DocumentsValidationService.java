package ch.sbb.line.directory.service.hearing;

import static ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants.MAX_DOCUMENTS;
import static ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants.MAX_DOCUMENTS_SIZE;

import ch.sbb.line.directory.service.exception.PdfDocumentConstraintViolationException;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DocumentsValidationService {

  private final TikaService tikaService;

  public DocumentsValidationService(TikaService tikaService) {
    this.tikaService = tikaService;
  }

  public void validateAllFilessArePdfs(List<File> files) {
    List<String> documentFileNames = files.stream()
      .map(tikaService::checkForPdf)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .toList();
    if (!documentFileNames.isEmpty()) {
      String exceptionMessage = documentFileNames.stream()
        .map(documentName -> "The given document: " + documentName + " is not a valid PDF file.")
        .collect(Collectors.joining(File.separator));
      throw new PdfDocumentConstraintViolationException(exceptionMessage);
    }
  }

  public void validateMaxSizeOfFiles(List<File> files) {
    long filesSize = files.stream()
      .map(File::length)
      .mapToLong(Long::longValue)
      .sum();
    if (filesSize > MAX_DOCUMENTS_SIZE) {
      String exceptionMessage = "The combined size of all documents in bytes is: " + filesSize + " which exceeds the maximum allowed size of 20MB.";
      throw new PdfDocumentConstraintViolationException(exceptionMessage);
    }
  }

  public void validateMaxNumberOfFiles(Integer numberOfFiles) {
    if (numberOfFiles > MAX_DOCUMENTS) {
      String exceptionMessage = "Overall number of documents is: " + numberOfFiles + " which exceeds the number of allowed documents of 3.";
      throw new PdfDocumentConstraintViolationException(exceptionMessage);
    }
  }

}
