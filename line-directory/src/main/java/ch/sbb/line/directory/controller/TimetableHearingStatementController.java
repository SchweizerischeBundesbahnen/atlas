package ch.sbb.line.directory.controller;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiV1;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementRequestParams;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.line.directory.entity.StatementDocument;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.mapper.TimeTableHearingStatementMapper;
import ch.sbb.line.directory.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.service.exception.PdfDocumentConstraintViolationException;
import ch.sbb.line.directory.service.hearing.ResponsibleTransportCompaniesResolverService;
import ch.sbb.line.directory.service.hearing.TikaService;
import ch.sbb.line.directory.service.hearing.TimetableFieldNumberResolverService;
import ch.sbb.line.directory.service.hearing.TimetableHearingStatementService;
import ch.sbb.line.directory.service.hearing.TimetableHearingYearService;
import ch.sbb.line.directory.service.upload.TimetableHearingPdfsUploadService;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TimetableHearingStatementController implements TimetableHearingStatementApiV1 {

    private final TimetableHearingStatementService timetableHearingStatementService;
    private final TimetableHearingYearService timetableHearingYearService;
    private final TimetableFieldNumberResolverService timetableFieldNumberResolverService;
    private final ResponsibleTransportCompaniesResolverService responsibleTransportCompaniesResolverService;

    private final FileService fileService;

    private final TimetableHearingPdfsUploadService timetableHearingPdfsUploadService;

    private final TikaService tikaService;

    @Override
    public Container<TimetableHearingStatementModel> getStatements(Pageable pageable,
        TimetableHearingStatementRequestParams statementRequestParams) {
        Page<TimetableHearingStatement> hearingStatements = timetableHearingStatementService.getHearingStatements(
            TimetableHearingStatementSearchRestrictions.builder()
                .pageable(pageable)
                .statementRequestParams(statementRequestParams).build());
        return Container.<TimetableHearingStatementModel>builder()
            .objects(hearingStatements.stream().map(TimeTableHearingStatementMapper::toModel).toList())
            .totalCount(hearingStatements.getTotalElements())
            .build();
    }

    @Override
    public TimetableHearingStatementModel getStatement(Long id) {
        return TimeTableHearingStatementMapper.toModel(timetableHearingStatementService.getStatementById(id));
    }

    @Override
    public TimetableHearingStatementModel createStatement(TimetableHearingStatementModel statement, List<MultipartFile> documents) {
        TimetableHearingStatement statementToCreate = TimeTableHearingStatementMapper.toEntity(statement);

        List<File> files = documents.stream()
            .map(fileService::getFileFromMultipart)
            .toList();
        validatePdfDocuments(files);

        timetableHearingPdfsUploadService.uploadPdfFile(files);

        addFilesToStatement(documents, statementToCreate);

        TimetableHearingStatement hearingStatement = timetableHearingStatementService.createHearingStatement(statementToCreate);

        return TimeTableHearingStatementMapper.toModel(hearingStatement);
    }

    @Override
    public TimetableHearingStatementModel createStatementExternal(TimetableHearingStatementModel statement,
        List<MultipartFile> documents) {
        String resolvedTtfnid =
            timetableFieldNumberResolverService.resolveTtfnid(statement.getTimetableFieldNumber());
        statement.setTtfnid(resolvedTtfnid);

        List<TimetableHearingStatementResponsibleTransportCompanyModel> responsibleTransportCompanies =
            responsibleTransportCompaniesResolverService.resolveResponsibleTransportCompanies(
                resolvedTtfnid);
        statement.setResponsibleTransportCompanies(responsibleTransportCompanies);

        Long activeHearingYear = timetableHearingYearService.getActiveHearingYear().getTimetableYear();
        statement.setTimetableYear(activeHearingYear);

        return createStatement(statement, documents);
    }

    @Override
    public TimetableHearingStatementModel updateHearingStatement(Long id, TimetableHearingStatementModel statement,
        List<MultipartFile> documents) {
        timetableHearingStatementService.getStatementById(id);

        TimetableHearingStatement statementUpdate = TimeTableHearingStatementMapper.toEntity(statement);
        addFilesToStatement(documents, statementUpdate);

        TimetableHearingStatement hearingStatement = timetableHearingStatementService.updateHearingStatement(statementUpdate);
        return TimeTableHearingStatementMapper.toModel(hearingStatement);
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

    private void validatePdfDocuments(List<File> documents) {
        // check number of documents
        if (documents.size() > 3) {
            throw new PdfDocumentConstraintViolationException("The number of received documents is: " + documents.size() + " which exceeds the number of allowed documents of 3.");
        }
        // check documents size
        long combinedDocumentsSize = documents.stream().map(File::length).mapToLong(Long::longValue).sum();
        if (combinedDocumentsSize > 20000000L) {
            throw new PdfDocumentConstraintViolationException("The combined size of received documents in bytes is: " + combinedDocumentsSize + " which exceeds the maximum allowed size of 20MB.");
        }
        // check if all documents are pdf
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

}
