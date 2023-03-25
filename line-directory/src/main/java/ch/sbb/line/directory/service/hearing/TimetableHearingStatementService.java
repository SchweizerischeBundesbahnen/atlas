package ch.sbb.line.directory.service.hearing;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.StatementDocument;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final TimetableHearingPdfsAmazonService pdfsUploadAmazonService;
    private final DocumentsValidationService documentsValidationService;

    public Page<TimetableHearingStatement> getHearingStatements(TimetableHearingStatementSearchRestrictions searchRestrictions) {
        return timetableHearingStatementRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
    }

    public TimetableHearingStatement getStatementById(Long id) {
        return timetableHearingStatementRepository.findById(id)
            .orElseThrow(() -> new IdNotFoundException(id));
    }

    public File getStatementDocument(Long id, String filename) {
        TimetableHearingStatement statementById = getStatementById(id);
        Optional<StatementDocument> statementDocument = getStatementDocument(filename, statementById);
        if (statementDocument.isPresent()) {
            return pdfsUploadAmazonService.downloadPdfFile(id.toString(), filename);
        } else {
            throw new FileNotFoundException(filename);
        }
    }

    public TimetableHearingStatement createHearingStatement(TimetableHearingStatement statementToCreate, List<MultipartFile> documents) {
        checkThatTimetableHearingYearExists(statementToCreate);
        statementToCreate.setStatementStatus(StatementStatus.RECEIVED);

        List<File> files = new ArrayList<>();
        if (documents != null && !documents.isEmpty()) {
            files = getFilesFromMultipartFiles(documents);
            documentsValidationService.validateMaxFiles(files.size(), 0);
            documentsValidationService.validateMaxFilesSize(files, Collections.emptyList());
            documentsValidationService.validateAllDocsArePdf(files);

            addFilesToStatement(documents, statementToCreate);
        }

        TimetableHearingStatement timetableHearingStatement = timetableHearingStatementRepository.save(statementToCreate);

        pdfsUploadAmazonService.uploadPdfFile(files, timetableHearingStatement.getId().toString());

        return timetableHearingStatement;
    }

    public TimetableHearingStatement updateHearingStatement(TimetableHearingStatement updatingTimetableHearingStatement,
        TimetableHearingStatement existingTimetableHearingStatement, List<MultipartFile> documents) {
        checkThatTimetableHearingYearExists(updatingTimetableHearingStatement);

        List<File> newFiles = new ArrayList<>();
        TimetableHearingStatement updatedObject;

        if (documents != null && !documents.isEmpty() && !Objects.requireNonNull(documents.get(0).getOriginalFilename()).isBlank()) {
            newFiles = getFilesFromMultipartFiles(documents);

            documentsValidationService.validateAllDocsArePdf(newFiles);
            Set<StatementDocument> existingFiles = existingTimetableHearingStatement.getDocuments();
            if (existingFiles != null && !existingFiles.isEmpty()) {
                documentsValidationService.validateMaxFiles(newFiles.size(), existingFiles.size());
                List<Long> existingDocsSizes = existingFiles.stream()
                    .map(StatementDocument::getFileSize)
                    .toList();
                documentsValidationService.validateMaxFilesSize(newFiles, existingDocsSizes);
                documentsValidationService.checkOverlappingFileNames(newFiles, existingFiles);
            } else {
                documentsValidationService.validateMaxFiles(newFiles.size(), 0);
                documentsValidationService.validateMaxFilesSize(newFiles, Collections.emptyList());
                documentsValidationService.checkOverlappingFileNames(newFiles, Collections.emptySet());
            }

            updatedObject = updateObject(updatingTimetableHearingStatement, existingTimetableHearingStatement);
            addFilesToStatement(documents, updatedObject);
        } else {
            updatedObject = updateObject(updatingTimetableHearingStatement, existingTimetableHearingStatement);
        }
        TimetableHearingStatement timetableHearingStatement = timetableHearingStatementRepository.save(updatedObject);
        pdfsUploadAmazonService.uploadPdfFile(newFiles, timetableHearingStatement.getId().toString());

        return timetableHearingStatement;
    }

    public void deleteDocument(Long id, String filename) {
        TimetableHearingStatement statementById = getStatementById(id);
        Optional<StatementDocument> statementDocument = getStatementDocument(filename, statementById);
        if (statementDocument.isPresent()) {
            pdfsUploadAmazonService.deletePdfFile(id.toString(), filename);
        } else {
            throw new FileNotFoundException(filename);
        }
    }

    private Optional<StatementDocument> getStatementDocument(String filename, TimetableHearingStatement statementById) {
        return statementById.getDocuments().stream()
            .filter(document -> document.getFileName().equals(filename))
            .findFirst();
    }

    private List<File> getFilesFromMultipartFiles(List<MultipartFile> documents) {
        return documents.stream()
            .map(fileService::getFileFromMultipart)
            .toList();
    }

    private TimetableHearingStatement updateObject(TimetableHearingStatement updatingTimetableHearingStatement,
        TimetableHearingStatement existingTimetableHearingStatement) {
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

    private void checkThatTimetableHearingYearExists(TimetableHearingStatement statement) {
        if (!timetableHearingYearRepository.existsById(statement.getTimetableYear())) {
            throw new IdNotFoundException(statement.getTimetableYear());
        }
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

    private void addFilesToStatement2(List<MultipartFile> documents, TimetableHearingStatement statement) {
        List<StatementDocument> statementDocuments = new ArrayList<>();
        if (documents != null) {
            log.info("Statement {}, adding {} documents", statement.getId() == null ? "new" : statement.getId(), documents.size());
            documents.forEach(multipartFile ->
                statementDocuments.add
                    (StatementDocument.builder().statement(statement).fileName(multipartFile.getOriginalFilename()).fileSize(multipartFile.getSize()).build())
            );
        }
        Set targetSet = Set.copyOf(statementDocuments);
        statement.setDocuments(targetSet);
    }
}
