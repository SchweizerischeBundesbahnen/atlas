package ch.sbb.line.directory.service.hearing;

import static ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants.MAX_DOCUMENTS_SIZE;
import static ch.sbb.line.directory.mapper.TimetableHearingStatementMapper.transformToCommaSeparated;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementDocumentModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementResponsibleTransportCompanyModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.kafka.model.SwissCanton;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.transport.company.entity.SharedTransportCompany;
import ch.sbb.line.directory.entity.StatementDocument;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.mapper.ResponsibleTransportCompanyMapper;
import ch.sbb.line.directory.mapper.StatementSenderMapper;
import ch.sbb.line.directory.mapper.TimetableHearingStatementMapper;
import ch.sbb.line.directory.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
  private final StatementDocumentFilesValidationService statementDocumentFilesValidationService;
  private final ResponsibleTransportCompanyMapper responsibleTransportCompanyMapper;
  private final TimetableHearingStatementMapper timetableHearingStatementMapper;

  public Page<TimetableHearingStatement> getHearingStatements(TimetableHearingStatementSearchRestrictions searchRestrictions) {
    log.info("Loading statements using {}", searchRestrictions);
    return timetableHearingStatementRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public TimetableHearingStatement getTimetableHearingStatementById(Long id) {
    return timetableHearingStatementRepository.findById(id)
      .orElseThrow(() -> new IdNotFoundException(id));
  }

  public File getStatementDocument(Long timetableHearingStatementId, String documentFilename) {
    TimetableHearingStatement timetableHearingStatement = getTimetableHearingStatementById(timetableHearingStatementId);
    if (timetableHearingStatement.checkIfStatementDocumentExists(documentFilename)) {
      return pdfsUploadAmazonService.downloadPdfFile(timetableHearingStatementId.toString(), documentFilename);
    } else {
      throw new FileNotFoundException(documentFilename);
    }
  }

  public TimetableHearingStatementModel createHearingStatement(TimetableHearingStatementModel statement,
      List<MultipartFile> documents) {
    TimetableHearingStatement statementToCreate = timetableHearingStatementMapper.toEntity(statement);
    checkThatTimetableHearingYearExists(statementToCreate.getTimetableYear());
    statementToCreate.setStatementStatus(StatementStatus.RECEIVED);

    List<File> files = new ArrayList<>();

    if (!CollectionUtils.isEmpty(documents)) {
      files = getFilesFromMultipartFiles(documents);
      filesValidation(files, Collections.emptySet());

      addFilesToStatement(documents, statementToCreate);
    }

    TimetableHearingStatement timetableHearingStatement = timetableHearingStatementRepository.saveAndFlush(statementToCreate);

    pdfsUploadAmazonService.uploadPdfFiles(files, timetableHearingStatement.getId().toString());

    return TimetableHearingStatementMapper.toModel(timetableHearingStatement);
  }

  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING, #existingStatement)")
  public TimetableHearingStatement updateHearingStatement(TimetableHearingStatement existingStatement, TimetableHearingStatementModel timetableHearingStatementModel, List<MultipartFile> documents) {
    checkThatTimetableHearingYearExists(timetableHearingStatementModel.getTimetableYear());

    TimetableHearingStatement timetableHearingStatementInDb = timetableHearingStatementRepository.getReferenceById(
        timetableHearingStatementModel.getId());

    Set<String> fileNamesToKeep = timetableHearingStatementModel.getDocuments().stream()
        .map(TimetableHearingStatementDocumentModel::getFileName).collect(Collectors.toSet());
    List<StatementDocument> documentsToDelete = timetableHearingStatementInDb.getDocuments().stream()
        .filter(document -> !fileNamesToKeep.contains(document.getFileName())).toList();
    documentsToDelete.forEach(document -> {
      timetableHearingStatementInDb.removeDocument(document.getFileName());
      pdfsUploadAmazonService.deletePdfFile(timetableHearingStatementModel.getId().toString(), document.getFileName());
    });

    List<File> files = new ArrayList<>();
    if (documents != null && !documents.isEmpty()) {
      files = getFilesFromMultipartFiles(documents);
      filesValidation(files, timetableHearingStatementInDb.getDocuments());
    }

    TimetableHearingStatement updatedObject = updateObject(timetableHearingStatementModel, timetableHearingStatementInDb);
    addFilesToStatement(documents, updatedObject);

    TimetableHearingStatement timetableHearingStatement = timetableHearingStatementRepository.save(updatedObject);
    pdfsUploadAmazonService.uploadPdfFiles(files, timetableHearingStatement.getId().toString());

    return timetableHearingStatement;
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
    statementDocumentFilesValidationService.validateAllFilessArePdfs(files);
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

  private List<File> getFilesFromMultipartFiles(List<MultipartFile> documents) {
      return documents.stream()
        .map(fileService::getFileFromMultipart)
        .toList();
  }

  private TimetableHearingStatement updateObject(TimetableHearingStatementModel timetableHearingStatementModel,
    TimetableHearingStatement timetableHearingStatementInDb) {
    timetableHearingStatementInDb.setTimetableYear(timetableHearingStatementModel.getTimetableYear());
    timetableHearingStatementInDb.setStatementStatus(timetableHearingStatementModel.getStatementStatus());
    timetableHearingStatementInDb.setTtfnid(timetableHearingStatementModel.getTtfnid());
    timetableHearingStatementInDb.setSwissCanton(timetableHearingStatementModel.getSwissCanton());
    timetableHearingStatementInDb.setStopPlace(timetableHearingStatementModel.getStopPlace());
    timetableHearingStatementInDb.setStatement(timetableHearingStatementModel.getStatement());
    timetableHearingStatementInDb.setJustification(timetableHearingStatementModel.getJustification());
    timetableHearingStatementInDb.setComment(timetableHearingStatementModel.getComment());
    timetableHearingStatementInDb.setStatementSender(
        StatementSenderMapper.toEntity(timetableHearingStatementModel.getStatementSender()));

    updateResponsibleTransportCompanies(timetableHearingStatementModel, timetableHearingStatementInDb);
    timetableHearingStatementInDb.setResponsibleTransportCompaniesDisplay(transformToCommaSeparated(timetableHearingStatementInDb));

    return timetableHearingStatementInDb;
  }

  private void updateResponsibleTransportCompanies(TimetableHearingStatementModel timetableHearingStatementModel,
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

  private void addFilesToStatement(List<MultipartFile> documents, TimetableHearingStatement statement) {
    if (documents != null) {
      log.info("Statement {}, adding {} documents", statement.getId() == null ? "new" : statement.getId(), documents.size());
      documents.forEach(multipartFile -> statement.addDocument(StatementDocument.builder()
        .fileName(multipartFile.getOriginalFilename())
        .fileSize(multipartFile.getSize())
        .build()));
    }
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
    statement.setStatementStatus(statementStatus);
    if (justification != null) {
      statement.setJustification(justification);
    }
    timetableHearingStatementRepository.save(statement);
  }

  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
      + ".ApplicationType).TIMETABLE_HEARING, #statement)")
  public void updateHearingCanton(TimetableHearingStatement statement, SwissCanton swissCanton, String comment) {
    statement.setSwissCanton(swissCanton);
    if (comment != null) {
      statement.setComment(comment);
    }
    timetableHearingStatementRepository.save(statement);
  }
}
