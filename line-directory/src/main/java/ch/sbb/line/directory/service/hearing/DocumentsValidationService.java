package ch.sbb.line.directory.service.hearing;

import static ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants.MAX_DOCUMENTS;
import static ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants.MAX_DOCUMENTS_SIZE;

import ch.sbb.line.directory.entity.StatementDocument;
import ch.sbb.line.directory.service.exception.PdfDocumentConstraintViolationException;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DocumentsValidationService {

    private final TikaService tikaService;

    public DocumentsValidationService(TikaService tikaService) {
        this.tikaService = tikaService;
    }

    public void validateAllDocsArePdf(List<File> documents) {
        List<String> documentFileNames = documents.stream()
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

    public void validateMaxFilesSize(List<File> newFilesSize, List<Long> existingFilesSize) {
        long sumNewFilesSize = newFilesSize.stream()
            .map(File::length)
            .mapToLong(Long::longValue)
            .sum();
        long sumExistingFilesSize = existingFilesSize.stream()
            .reduce(0L, Long::sum);
        long allFilesSize = sumNewFilesSize + sumExistingFilesSize;
        if (allFilesSize > MAX_DOCUMENTS_SIZE * 1000000L) {
            String exceptionMessage = "The combined size of all documents in bytes is: " + allFilesSize + " which exceeds the maximum allowed size of 20MB.";
            throw new PdfDocumentConstraintViolationException(exceptionMessage);
        }
    }

    public void validateMaxFiles(Integer newFiles, Integer existingFiles) {
        int allFiles = newFiles + existingFiles;
        if (allFiles > MAX_DOCUMENTS) {
            String exceptionMessage = "Overall number of documents is: " + allFiles + " which exceeds the number of allowed documents of 3.";
            throw new PdfDocumentConstraintViolationException(exceptionMessage);
        }
    }

    public void checkOverlappingFileNames(List<File> newFiles, Set<StatementDocument> existingFiles) {
        List<String> newFileNames = newFiles.stream()
            .map(File::getName)
            .toList();

        List<StatementDocument> overlappingFileNames = existingFiles.stream()
            .toList()
            .stream()
            .filter(doc -> newFileNames.contains(doc.getFileName()))
            .toList();

        if (!overlappingFileNames.isEmpty()) {
            String exceptionMessage = overlappingFileNames.stream()
                .map(file -> "The given document name: " + file.getFileName() + " already exist in your documents, please rename your document.")
                .collect(Collectors.joining(File.separator));
            throw new PdfDocumentConstraintViolationException(exceptionMessage);
        }
    }
}
