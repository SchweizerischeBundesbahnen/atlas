package ch.sbb.line.directory.module.tth.service;

import static ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants.MAX_DOCUMENTS_SIZE;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementDataProtectionModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementDocumentModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModelV2;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.api.timetable.hearing.model.BatchUpdateTimetableHearingStatementsModel;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.model.exception.SimpleAtlasException;
import ch.sbb.atlas.pdf.sanitize.PdfCdr;
import ch.sbb.line.directory.exception.TtfnidNotFoundException;
import ch.sbb.line.directory.module.ttfn.repository.TimetableFieldNumberRepository;
import ch.sbb.line.directory.module.tth.entity.StatementDocument;
import ch.sbb.line.directory.module.tth.entity.TimetableHearingStatement;
import ch.sbb.line.directory.module.tth.exception.StatementPartOfDossierException;
import ch.sbb.line.directory.module.tth.mapper.ResponsibleTransportCompanyMapper;
import ch.sbb.line.directory.module.tth.mapper.StatementSenderMapperV2;
import ch.sbb.line.directory.module.tth.mapper.TimetableHearingStatementMapperV2;
import ch.sbb.line.directory.module.tth.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.module.tth.redact.TthStatementRedacted;
import ch.sbb.line.directory.module.tth.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.module.tth.repository.TimetableHearingYearRepository;
import ch.sbb.line.directory.shared.transportcompany.entity.SharedTransportCompany;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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
  private final TimetableFieldNumberRepository timetableFieldNumberRepository;
  private final FileService fileService;
  private final TimetableHearingPdfsAmazonService pdfsUploadAmazonService;
  private final StatementDocumentFilesValidationService statementDocumentFilesValidationService;
  private final ResponsibleTransportCompanyMapper responsibleTransportCompanyMapper;
  private final TimetableHearingStatementMapperV2 timetableHearingStatementMapperV2;

  public Page<TimetableHearingStatement> getHearingStatements(TimetableHearingStatementSearchRestrictions searchRestrictions) {
    log.info("Loading statements using {}", searchRestrictions);
    return timetableHearingStatementRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public void removeDossierRelationsAndStatusToReceivedFor(List<Long> statementIds) {
    timetableHearingStatementRepository.removeDossierRelationAndSetReceivedFor(statementIds);
  }

  @TthStatementRedacted
  @PostAuthorize("""
      @cantonBasedUserAdministrationService.isAtLeastExplicitReader(T(ch.sbb.atlas.kafka.model.user.admin.ApplicationType).TIMETABLE_HEARING)
      or
      @boUserMailCheckService.isCurrentUserMailAssignedTo(returnObject)""")
  public TimetableHearingStatement getTimetableHearingStatementById(Long id) {
    return timetableHearingStatementRepository.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));
  }

  public File getStatementDocument(TimetableHearingStatement timetableHearingStatement, String documentFilename) {
    if (timetableHearingStatement.checkIfStatementDocumentExists(documentFilename)) {
      return pdfsUploadAmazonService.downloadPdfFile(timetableHearingStatement.getId().toString(), documentFilename);
    } else {
      throw new FileNotFoundException(documentFilename);
    }
  }

  public TimetableHearingStatement createHearingStatement(TimetableHearingStatement statementToCreate,
      List<MultipartFile> documents) {
    checkThatTimetableHearingYearExists(statementToCreate.getTimetableYear());
    checkThatTimetableFieldNumberExists(statementToCreate);

    statementToCreate.setStatementStatus(StatementStatus.RECEIVED);

    List<File> files = getFilesFromMultipartFiles(documents, Collections.emptySet());
    addFilesToStatement(files, statementToCreate);

    TimetableHearingStatement timetableHearingStatement = timetableHearingStatementRepository.saveAndFlush(statementToCreate);

    pdfsUploadAmazonService.uploadPdfFiles(files, timetableHearingStatement.getId().toString());

    return timetableHearingStatement;
  }

  public TimetableHearingStatementModelV2 createHearingStatementV2(TimetableHearingStatementModelV2 statement,
      List<MultipartFile> documents) {
    TimetableHearingStatement statementToCreate = timetableHearingStatementMapperV2.toEntity(statement);
    return TimetableHearingStatementMapperV2.toModel(createHearingStatement(statementToCreate, documents));
  }

  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING, #existingStatement)")
  public TimetableHearingStatement updateHearingStatement(TimetableHearingStatement existingStatement,
      TimetableHearingStatementModelV2 timetableHearingStatementModel, List<MultipartFile> documents) {
    checkThatStatementIsNotPartOfDossier(existingStatement);
    checkThatTimetableHearingYearExists(timetableHearingStatementModel.getTimetableYear());

    TimetableHearingStatement timetableHearingStatementInDb = timetableHearingStatementRepository.findById(
            timetableHearingStatementModel.getId())
        .orElseThrow(() -> new IdNotFoundException(timetableHearingStatementModel.getId()));

    updateStatementDocuments(timetableHearingStatementModel, timetableHearingStatementInDb);

    Set<String> fileNamesToKeep = timetableHearingStatementModel.getDocuments().stream()
        .map(TimetableHearingStatementDocumentModel::getFileName).collect(Collectors.toSet());
    List<StatementDocument> documentsToDelete = timetableHearingStatementInDb.getDocuments().stream()
        .filter(document -> !fileNamesToKeep.contains(document.getFileName())).toList();
    documentsToDelete.forEach(document -> {
      timetableHearingStatementInDb.removeDocument(document.getFileName());
      pdfsUploadAmazonService.deletePdfFile(timetableHearingStatementModel.getId().toString(), document.getFileName());
    });

    List<File> files = getFilesFromMultipartFiles(documents, timetableHearingStatementInDb.getDocuments());

    TimetableHearingStatement updatedObject = updateObject(timetableHearingStatementModel, timetableHearingStatementInDb);
    addFilesToStatement(files, updatedObject);
    if (!files.isEmpty()) {
      updatedObject.setDataProtectionChecked(false);
    }
    checkThatTimetableFieldNumberExists(updatedObject);

    TimetableHearingStatement timetableHearingStatement = timetableHearingStatementRepository.save(updatedObject);
    pdfsUploadAmazonService.uploadPdfFiles(files, timetableHearingStatement.getId().toString());

    return timetableHearingStatement;
  }

  private void updateStatementDocuments(TimetableHearingStatementModelV2 timetableHearingStatementModel,
      TimetableHearingStatement timetableHearingStatementInDb) {
    timetableHearingStatementModel.getDocuments()
        .forEach(document -> timetableHearingStatementInDb.getDocuments()
            .stream()
            .filter(statementDocument -> statementDocument.getId().equals(document.getId()))
            .findFirst()
            .ifPresent(statementDocument -> statementDocument.setAnonymous(document.getAnonymous())));
  }

  public void deleteSpamMailFromYear(Long timetableHearingYear) {
    timetableHearingStatementRepository.deleteByStatementStatusAndTimetableYear(StatementStatus.JUNK, timetableHearingYear);
  }

  public void moveClosedStatementsToNextYearWithStatusUpdates(Long timetableHearingYear) {
    List<TimetableHearingStatement> statements = timetableHearingStatementRepository.findAllByStatementStatusInAndTimetableYear(
        List.of(StatementStatus.RECEIVED, StatementStatus.IN_REVIEW, StatementStatus.MOVED),
        timetableHearingYear
    );
    final Long nextYear = timetableHearingYear + 1;
    statements.forEach(statement -> {
      if (statement.getStatementStatus() == StatementStatus.MOVED) {
        statement.setStatementStatus(StatementStatus.RECEIVED);
      }
      statement.setTimetableYear(nextYear);
    });
    timetableHearingStatementRepository.saveAll(statements);
  }

  private void filesValidation(List<File> files, Set<StatementDocument> alreadySavedDocuments) {
    log.info("Starting files validation for files {}", files.stream().map(File::getName).toList());
    statementDocumentFilesValidationService.validateMaxNumberOfFiles(files.size() + alreadySavedDocuments.size());
    statementDocumentFilesValidationService.validateNoFileNameDuplicate(files, alreadySavedDocuments);
    statementDocumentFilesValidationService.validateMaxSizeOfFiles(files, alreadySavedDocuments, MAX_DOCUMENTS_SIZE);

    log.info("Starting PDF filetype validation.");
    statementDocumentFilesValidationService.validateAllFilesArePdfs(files);
    log.info("Concluded files validation.");
  }

  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING, #timetableHearingStatement)")
  public void deleteStatementDocument(TimetableHearingStatement timetableHearingStatement, String documentFilename) {
    if (timetableHearingStatement.getId() == null || StringUtils.isBlank(documentFilename)) {
      throw new IllegalArgumentException();
    }
    if (timetableHearingStatement.checkIfStatementDocumentExists(documentFilename)) {
      removeDocumentFromS3andDB(documentFilename, timetableHearingStatement);
    }
  }

  private void removeDocumentFromS3andDB(String documentFilename, TimetableHearingStatement timetableHearingStatement) {
    timetableHearingStatement.removeDocument(documentFilename);
    timetableHearingStatementRepository.save(timetableHearingStatement);
    pdfsUploadAmazonService.deletePdfFile(timetableHearingStatement.getId().toString(), documentFilename);
  }

  private List<File> getFilesFromMultipartFiles(List<MultipartFile> documents, Set<StatementDocument> alreadySavedDocuments) {
    List<File> files = new ArrayList<>();
    if (documents != null && !documents.isEmpty()) {
      files = documents.stream()
          .map(fileService::getFileFromMultipart)
          .toList();
      filesValidation(files, alreadySavedDocuments);
    }
    files.forEach(PdfCdr::sanitize);
    return files;
  }

  private TimetableHearingStatement updateObject(TimetableHearingStatementModelV2 timetableHearingStatementModel,
      TimetableHearingStatement timetableHearingStatementInDb) {
    timetableHearingStatementInDb.setTimetableYear(timetableHearingStatementModel.getTimetableYear());
    timetableHearingStatementInDb.setStatementStatus(timetableHearingStatementModel.getStatementStatus());
    timetableHearingStatementInDb.setTtfnid(timetableHearingStatementModel.getTtfnid());
    timetableHearingStatementInDb.setSwissCanton(timetableHearingStatementModel.getSwissCanton());

    timetableHearingStatementInDb.setOldSwissCanton(timetableHearingStatementModel.getOldSwissCanton());

    timetableHearingStatementInDb.setStopPlace(timetableHearingStatementModel.getStopPlace());
    timetableHearingStatementInDb.setStatement(timetableHearingStatementModel.getStatement());
    timetableHearingStatementInDb.setPublicComment(timetableHearingStatementModel.getPublicComment());
    timetableHearingStatementInDb.setInternalComment(timetableHearingStatementModel.getInternalComment());
    timetableHearingStatementInDb.setCantonTransferComment(timetableHearingStatementModel.getCantonTransferComment());
    timetableHearingStatementInDb.setStatementSender(
        StatementSenderMapperV2.toEntity(timetableHearingStatementModel.getStatementSender()));
    timetableHearingStatementInDb.setTopic(timetableHearingStatementModel.getTopic());
    timetableHearingStatementInDb.setStatementAnonymous(timetableHearingStatementModel.getStatementAnonymous());
    timetableHearingStatementInDb.setAnonymousStatement(timetableHearingStatementModel.getAnonymousStatement());

    updateResponsibleTransportCompanies(timetableHearingStatementModel, timetableHearingStatementInDb);
    timetableHearingStatementInDb.setResponsibleTransportCompaniesDisplay(
        timetableHearingStatementInDb.getTransportCompaniesCommaSeparated());

    return timetableHearingStatementInDb;
  }

  private void updateResponsibleTransportCompanies(TimetableHearingStatementModelV2 timetableHearingStatementModel,
      TimetableHearingStatement timetableHearingStatementInDb) {
    Set<Long> desiredTransportCompanies = timetableHearingStatementModel.getResponsibleTransportCompanies().stream().map(
        TimetableHearingStatementResponsibleTransportCompanyModel::getId).collect(Collectors.toSet());
    Set<Long> currentTransportCompanies = timetableHearingStatementInDb.getResponsibleTransportCompanies().stream()
        .map(SharedTransportCompany::getId).collect(Collectors.toSet());

    Set<SharedTransportCompany> transportCompaniesToRemove = new HashSet<>();
    timetableHearingStatementInDb.getResponsibleTransportCompanies().forEach(responsibleTransportCompany -> {
      if (!desiredTransportCompanies.contains(responsibleTransportCompany.getId())) {
        transportCompaniesToRemove.add(responsibleTransportCompany);
      }
    });
    timetableHearingStatementInDb.getResponsibleTransportCompanies().removeAll(transportCompaniesToRemove);
    timetableHearingStatementModel.getResponsibleTransportCompanies().forEach(responsibleTransportCompany -> {
      if (!currentTransportCompanies.contains(responsibleTransportCompany.getId())) {
        timetableHearingStatementInDb.getResponsibleTransportCompanies()
            .add(responsibleTransportCompanyMapper.toEntity(responsibleTransportCompany));
      }
    });
  }

  private void checkThatTimetableHearingYearExists(Long timetableYear) {
    if (!timetableHearingYearRepository.existsById(timetableYear)) {
      throw new IdNotFoundException(timetableYear);
    }
  }

  private void checkThatTimetableFieldNumberExists(TimetableHearingStatement statement) {
    if (statement.getTtfnid() != null && !statement.getTtfnid().isEmpty() &&
        !timetableFieldNumberRepository.existsByTtfnid(statement.getTtfnid())) {
      throw new TtfnidNotFoundException(statement.getTtfnid());
    }
  }

  private void checkThatStatementIsNotPartOfDossier(TimetableHearingStatement statement) {
    if (statement.isPartOfDossier()) {
      throw new StatementPartOfDossierException();
    }
  }

  private void addFilesToStatement(List<File> documents, TimetableHearingStatement statement) {
    log.info("Statement {}, adding {} documents", statement.getId() == null ? "new" : statement.getId(), documents.size());
    documents.forEach(file -> statement.addDocument(StatementDocument.builder()
        .fileName(file.getName())
        .fileSize(file.length())
        .build()));
  }

  public List<TimetableHearingStatement> getTimetableHearingStatementsByIds(List<Long> ids) {
    return timetableHearingStatementRepository.findAllById(ids);
  }

  public TimetableHearingStatement getTimetableHearingStatementsById(Long id) {
    return timetableHearingStatementRepository.findById(id).orElseThrow(() -> new IdNotFoundException(id));
  }

  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING, #statement)")
  public void updateHearingStatementStatus(TimetableHearingStatement statement, StatementStatus statementStatus,
      String justification) {
    checkThatStatementIsNotPartOfDossier(statement);
    statement.setStatementStatus(statementStatus);
    if (justification != null) {
      statement.setInternalComment(justification);
    }
    timetableHearingStatementRepository.save(statement);
  }

  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING, #statement)")
  public void updateHearingCanton(TimetableHearingStatement statement, SwissCanton swissCanton, String comment) {
    checkThatStatementIsNotPartOfDossier(statement);
    statement.setOldSwissCanton(statement.getSwissCanton());
    statement.setSwissCanton(swissCanton);
    if (comment != null) {
      statement.setCantonTransferComment(comment);
    }
    timetableHearingStatementRepository.save(statement);
  }

  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING, #statement)")
  public void updateStatementFromDossier(TimetableHearingStatement statement,
      BatchUpdateTimetableHearingStatementsModel updateModel) {
    if (isStatementAlreadyPartOfAnotherDossier(statement, updateModel)) {
      throw new StatementPartOfDossierException();
    }
    if (updateModel.getDossierCanton() != statement.getSwissCanton()) {
      throw SimpleAtlasException.builder()
          .status(HttpStatus.PRECONDITION_FAILED)
          .messageAndError("Dossier canton does not match statement canton")
          .displayCode("TTH.DOSSIER_STATEMENT_CANTON_MISMATCH")
          .build();
    }

    statement.setStatementStatus(updateModel.getStatementStatus());
    statement.setDossierId(updateModel.getDossierId());
    statement.setDossierContactMail(updateModel.getDossierContactMail());
    statement.setDossierContactSbbuid(updateModel.getDossierContactSbbuid());
    statement.setPublicComment(updateModel.getPublicComment());
    statement.setInternalComment(updateModel.getInternalComment());
    statement.setTopic(updateModel.getTopic());
    timetableHearingStatementRepository.save(statement);
  }

  private static boolean isStatementAlreadyPartOfAnotherDossier(TimetableHearingStatement statement,
      BatchUpdateTimetableHearingStatementsModel updateModel) {
    return statement.isPartOfDossier()
        && updateModel.getDossierId() != null
        && !statement.getDossierId().equals(updateModel.getDossierId());
  }

  public void checkDataProtection(TimetableHearingStatementDataProtectionModel timetableHearingStatementDataProtectionModel) {
    TimetableHearingStatement statement = getTimetableHearingStatementById(timetableHearingStatementDataProtectionModel.getId());

    statement.setStatementAnonymous(timetableHearingStatementDataProtectionModel.getStatementAnonymous());
    statement.setAnonymousStatement(timetableHearingStatementDataProtectionModel.getAnonymousStatement());

    for (StatementDocument currentDocument : statement.getDocuments()) {
      timetableHearingStatementDataProtectionModel.getDocuments().stream()
          .filter(i -> i.getId().equals(currentDocument.getId()))
          .findFirst()
          .ifPresent(documentModel -> currentDocument.setAnonymous(documentModel.getAnonymous()));
    }

    statement.setDataProtectionChecked(true);
    timetableHearingStatementRepository.save(statement);
  }
}