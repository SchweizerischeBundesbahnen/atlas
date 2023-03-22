package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.model.exception.NotFoundException.FileFoundException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.StatementDocument;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.service.exception.PdfDocumentConstraintViolationException;
import ch.sbb.line.directory.service.upload.TimetableHearingPdfsUploadService;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TimetableHearingStatementService {

    private final TimetableHearingStatementRepository timetableHearingStatementRepository;
    private final TimetableHearingYearRepository timetableHearingYearRepository;
    private final FileService fileService;
    private final TimetableHearingPdfsUploadService pdfsUploadAmazonService;
    private final TikaService tikaService;
    @Value("${atlas.timetablehearing.max-number-pdfs}")
    private String maxNumberPdfs;
    @Value("${atlas.timetablehearing.max-size-pdfs}")
    private String maxSizePdfs;

    public Page<TimetableHearingStatement> getHearingStatements(TimetableHearingStatementSearchRestrictions searchRestrictions) {
        return timetableHearingStatementRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
    }

    public TimetableHearingStatement getStatementById(Long id) {
        return timetableHearingStatementRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
    }

    public File getStatementDocument(Long id, String filename) {
        TimetableHearingStatement statementById = getStatementById(id);
        Optional<StatementDocument> statementDocument = getStatementDocument(filename, statementById);
        if (statementDocument.isPresent()) {
            return pdfsUploadAmazonService.downloadPdfFile(id.toString(), filename);
        }
        else throw new FileFoundException(filename);
    }

    private Optional<StatementDocument> getStatementDocument(String filename, TimetableHearingStatement statementById) {
        Optional<StatementDocument> statementDocument = statementById.getDocuments().stream()
            .filter(document -> document.getFileName().equals(filename))
            .findFirst();
        return statementDocument;
    }

    public TimetableHearingStatement createHearingStatement(TimetableHearingStatement statementToCreate, List<MultipartFile> documents) {
        checkThatTimetableHearingYearExists(statementToCreate);
        statementToCreate.setStatementStatus(StatementStatus.RECEIVED);

        List<File> documentsInFileFormat = getFilesFromMultipartFiles(documents);
        validateAllowedNumberOfNewDocs(documentsInFileFormat, Collections.emptySet());
        validateAllowedSizeOfDocsSummary(documentsInFileFormat, Collections.emptyList());
        validateAllDocsArePdf(documentsInFileFormat);

        addFilesToStatement(documents, statementToCreate);
        TimetableHearingStatement timetableHearingStatement = timetableHearingStatementRepository.save(statementToCreate);

        pdfsUploadAmazonService.uploadPdfFile(documentsInFileFormat, timetableHearingStatement.getId().toString());

        return timetableHearingStatement;
    }

    public TimetableHearingStatement createHearingStatement(TimetableHearingStatement statementToCreate) {
        checkThatTimetableHearingYearExists(statementToCreate);

        statementToCreate.setStatementStatus(StatementStatus.RECEIVED);

        return timetableHearingStatementRepository.save(statementToCreate);
    }

    private List<File> getFilesFromMultipartFiles(List<MultipartFile> documents) {
        return documents.stream()
            .map(fileService::getFileFromMultipart)
            .toList();
    }

    public TimetableHearingStatement updateHearingStatement(TimetableHearingStatement updatingTimetableHearingStatement,
        TimetableHearingStatement existingTimetableHearingStatement, List<MultipartFile> newDocs) {
        checkThatTimetableHearingYearExists(updatingTimetableHearingStatement);

        List<File> newDocsInFileFormat = getFilesFromMultipartFiles(newDocs);

        validateAllDocsArePdf(newDocsInFileFormat);
        validateAllowedNumberOfNewDocs(newDocsInFileFormat, Collections.emptySet());

        Set<StatementDocument> existingDocs = existingTimetableHearingStatement.getDocuments();
        List<String> filesToDelete = checkTheNumberOfDeletingFiles(newDocsInFileFormat, existingDocs);
        // delete file from amazon and delete statement from db
        filesToDelete
            .forEach(file -> pdfsUploadAmazonService.deletePdfFile(existingTimetableHearingStatement.getId().toString(), file));

        List<Long> existingDocsSizes = existingDocs.stream()
            .map(StatementDocument::getFileSize)
            .toList();

        validateAllowedSizeOfDocsSummary(newDocsInFileFormat, existingDocsSizes);

        TimetableHearingStatement updatedObject = updateObject(updatingTimetableHearingStatement, existingTimetableHearingStatement);

        addFilesToStatement(newDocs, updatedObject);
        TimetableHearingStatement timetableHearingStatement = timetableHearingStatementRepository.save(updatedObject);
        pdfsUploadAmazonService.uploadPdfFile(newDocsInFileFormat, timetableHearingStatement.getId().toString());

        return timetableHearingStatement;
    }

    private TimetableHearingStatement updateObject(TimetableHearingStatement updatingTimetableHearingStatement, TimetableHearingStatement existingTimetableHearingStatement) {
        existingTimetableHearingStatement.setTimetableYear(updatingTimetableHearingStatement.getTimetableYear());
        existingTimetableHearingStatement.setStatementStatus(updatingTimetableHearingStatement.getStatementStatus());
        existingTimetableHearingStatement.setTtfnid(updatingTimetableHearingStatement.getTtfnid());
        existingTimetableHearingStatement.setSwissCanton(updatingTimetableHearingStatement.getSwissCanton());
        existingTimetableHearingStatement.setStopPlace(updatingTimetableHearingStatement.getStopPlace());
        existingTimetableHearingStatement.setStatement(updatingTimetableHearingStatement.getStatement());
        existingTimetableHearingStatement.setJustification(updatingTimetableHearingStatement.getJustification());
        existingTimetableHearingStatement.setStatementSender(updatingTimetableHearingStatement.getStatementSender());
        return existingTimetableHearingStatement;
    }

    private void checkDocumentNamesOverlapping(List<MultipartFile> newDocs, Set<StatementDocument> existingDocs) {
        List<String> newDocsNames = newDocs.stream()
            .map(MultipartFile::getOriginalFilename)
            .toList();

        List<StatementDocument> overlappingDocNames = existingDocs.stream().toList().stream()
            .filter(doc -> newDocsNames.contains(doc.getFileName()))
            .toList();

        if (!overlappingDocNames.isEmpty()) {
            String exceptionMessage = overlappingDocNames.stream()
                .map(docName -> "The given document name: " + docName.getFileName() + " already exist in your documents, please rename your document.")
                .collect(Collectors.joining(File.separator));
            throw new PdfDocumentConstraintViolationException(exceptionMessage);
        }
    }

    public TimetableHearingStatement updateHearingStatement(TimetableHearingStatement statementUpdate, TimetableHearingStatement existingStatement) {
        checkThatTimetableHearingYearExists(statementUpdate);

        TimetableHearingStatement updatedObject = updateObject(statementUpdate, existingStatement);

        return timetableHearingStatementRepository.save(updatedObject);
    }

    private void checkThatTimetableHearingYearExists(TimetableHearingStatement statement) {
        if (!timetableHearingYearRepository.existsById(statement.getTimetableYear())) {
            throw new IdNotFoundException(statement.getTimetableYear());
        }
    }

    private void validateAllDocsArePdf(List<File> documents) {
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

    private void validateAllowedSizeOfDocsSummary(List<File> newDocsSizes, List<Long> existingDocsSizes) {
        long combinedNewDocsSize = newDocsSizes.stream()
            .map(File::length)
            .mapToLong(Long::longValue)
            .sum();
        long combinedExistingDocsSize = existingDocsSizes.stream()
            .reduce(0L, Long::sum);
        long overallDocsSize = combinedNewDocsSize + combinedExistingDocsSize;
        if (overallDocsSize > Integer.parseInt(maxSizePdfs) * 1000000L) {
            String exceptionMessage = "The combined size of received documents in bytes is: " + combinedNewDocsSize + " which exceeds the maximum allowed size of 20MB.";
            throw new PdfDocumentConstraintViolationException(exceptionMessage);
        }
    }

    private void validateAllowedNumberOfNewDocs(List<File> newDocs, Set<StatementDocument> existingDocs) {
        int allDocsNumber = newDocs.size() + existingDocs.size();
        if (allDocsNumber > Integer.parseInt(maxNumberPdfs)) {
            String exceptionMessage = "Overall number of documents is: " + allDocsNumber + " which exceeds the number of allowed documents of 3.";
            throw new PdfDocumentConstraintViolationException(exceptionMessage);
        }
    }

    private List<String> checkTheNumberOfDeletingFiles(List<File> newDocs, Set<StatementDocument> existingDocs) {
        List<String> deletingFiles = new ArrayList<>();
        int allDocsNumber = newDocs.size() + existingDocs.size();
        if (allDocsNumber > Integer.parseInt(maxNumberPdfs)) {
            if (existingDocs.size() == 1) {
                deletingFiles.add(existingDocs.stream().toList().get(0).getFileName());
                return deletingFiles;
            } else if (existingDocs.size() == 2) {
                if (newDocs.size() == 2) {
                    deletingFiles.add(existingDocs.stream().toList().get(0).getFileName());
                    return deletingFiles;
                } else {
                    deletingFiles.add(existingDocs.stream().toList().get(0).getFileName());
                    deletingFiles.add(existingDocs.stream().toList().get(1).getFileName());
                    return deletingFiles;
                }
            } else {
                if (newDocs.size() == 1) {
                    deletingFiles.add(existingDocs.stream().toList().get(0).getFileName());
                    return deletingFiles;
                } else if (newDocs.size() == 2) {
                    deletingFiles.add(existingDocs.stream().toList().get(0).getFileName());
                    deletingFiles.add(existingDocs.stream().toList().get(1).getFileName());
                    return deletingFiles;
                } else {
                    deletingFiles.add(existingDocs.stream().toList().get(0).getFileName());
                    deletingFiles.add(existingDocs.stream().toList().get(1).getFileName());
                    deletingFiles.add(existingDocs.stream().toList().get(2).getFileName());
                    return deletingFiles;
                }
            }
        }
        return Collections.emptyList();
    }

    private void addFilesToStatement(List<MultipartFile> documents, TimetableHearingStatement statement) {
        if (documents != null) {
            log.info("Statement {}, adding {} documents", statement.getId() == null ? "new" : statement.getId(), documents.size());
            documents.forEach(multipartFile -> statement.getDocuments().add(StatementDocument.builder()
                .statement(statement)
                .fileName(multipartFile.getOriginalFilename())
                .fileSize(multipartFile.getSize())
                .build()));
        }
    }

}
