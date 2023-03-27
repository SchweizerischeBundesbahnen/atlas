package ch.sbb.line.directory.service.hearing;

import static ch.sbb.atlas.api.timetable.hearing.TimetableHearingConstants.MAX_DOCUMENTS_SIZE;

import ch.sbb.atlas.amazon.service.FileService;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementModel;
import ch.sbb.atlas.api.timetable.hearing.enumeration.StatementStatus;
import ch.sbb.atlas.model.exception.NotFoundException.FileNotFoundException;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.StatementDocument;
import ch.sbb.line.directory.entity.TimetableHearingStatement;
import ch.sbb.line.directory.mapper.StatementSenderMapper;
import ch.sbb.line.directory.mapper.TimeTableHearingStatementMapper;
import ch.sbb.line.directory.model.TimetableHearingStatementSearchRestrictions;
import ch.sbb.line.directory.repository.TimetableHearingStatementRepository;
import ch.sbb.line.directory.repository.TimetableHearingYearRepository;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
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

  public TimetableHearingStatement getTimetableHearingStatementById(Long id) {
    return timetableHearingStatementRepository.findById(id)
      .orElseThrow(() -> new IdNotFoundException(id));
  }

  public File getStatementDocument(Long timetableHearingStatementId, String documentFilename) {
    var timetableHearingStatement = getTimetableHearingStatementById(timetableHearingStatementId);
    if (timetableHearingStatement.checkIfStatementDocumentExists(documentFilename)) {
      return pdfsUploadAmazonService.downloadPdfFile(timetableHearingStatementId.toString(), documentFilename);
    } else {
      throw new FileNotFoundException(documentFilename);
    }
  }

  public TimetableHearingStatementModel createHearingStatement(TimetableHearingStatementModel statement, List<MultipartFile> documents) {
    TimetableHearingStatement statementToCreate = TimeTableHearingStatementMapper.toEntity(statement);
    checkThatTimetableHearingYearExists(statementToCreate.getTimetableYear());
    statementToCreate.setStatementStatus(StatementStatus.RECEIVED);

    List<File> files = new ArrayList<>();

    if (!CollectionUtils.isEmpty(documents)) {
      files = getFilesFromMultipartFiles(documents);
      documentsValidationService.validateMaxNumberOfFiles(files.size());
      documentsValidationService.validateMaxSizeOfFiles(files, MAX_DOCUMENTS_SIZE);
      documentsValidationService.validateAllFilessArePdfs(files);

      addFilesToStatement(documents, statementToCreate);
    }

    TimetableHearingStatement timetableHearingStatement = timetableHearingStatementRepository.save(statementToCreate);

    pdfsUploadAmazonService.uploadPdfFiles(files, timetableHearingStatement.getId().toString());

    return TimeTableHearingStatementMapper.toModel(timetableHearingStatement);
  }

  public TimetableHearingStatement updateHearingStatement(TimetableHearingStatementModel timetableHearingStatementModel, List<MultipartFile> documents) {
    checkThatTimetableHearingYearExists(timetableHearingStatementModel.getTimetableYear());

    var timetableHearingStatementInDb = timetableHearingStatementRepository.getReferenceById(timetableHearingStatementModel.getId());

    List<File> files = new ArrayList<>();
    if (documents != null && !documents.isEmpty()) {
      files = getFilesFromMultipartFiles(documents);
      documentsValidationService.validateMaxNumberOfFiles(files.size());
      documentsValidationService.validateMaxSizeOfFiles(files, MAX_DOCUMENTS_SIZE);
      documentsValidationService.validateAllFilessArePdfs(files);

      List<StatementDocument> statementDocumentsList = new ArrayList<>(timetableHearingStatementInDb.getDocuments());
      statementDocumentsList.forEach(statementDocument -> removeDocumentFromS3andDB(statementDocument.getFileName(), timetableHearingStatementInDb));
    }
    TimetableHearingStatement updatedObject = updateObject(timetableHearingStatementModel, timetableHearingStatementInDb);
    addFilesToStatement(documents, updatedObject);

    TimetableHearingStatement timetableHearingStatement = timetableHearingStatementRepository.save(updatedObject);
    pdfsUploadAmazonService.uploadPdfFiles(files, timetableHearingStatement.getId().toString());

    return timetableHearingStatement;
  }
  @PreAuthorize("@cantonBasedUserAdministrationService.isAtLeastWriter(T(ch.sbb.atlas.kafka.model.user.admin"
    + ".ApplicationType).TIMETABLE_HEARING, #timetableHearingStatement)")
  public void deleteStatementDocument(TimetableHearingStatement timetableHearingStatement, String documentFilename) {
    if (timetableHearingStatement.getId()==null || !StringUtils.hasText(documentFilename)) {
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
    TimetableHearingStatement timetableHearingStatement) {
    timetableHearingStatement.setTimetableYear(timetableHearingStatementModel.getTimetableYear());
    timetableHearingStatement.setStatementStatus(timetableHearingStatementModel.getStatementStatus());
    timetableHearingStatement.setTtfnid(timetableHearingStatementModel.getTtfnid());
    timetableHearingStatement.setSwissCanton(timetableHearingStatementModel.getSwissCanton());
    timetableHearingStatement.setStopPlace(timetableHearingStatementModel.getStopPlace());
    timetableHearingStatement.setStatement(timetableHearingStatementModel.getStatement());
    timetableHearingStatement.setJustification(timetableHearingStatementModel.getJustification());
    timetableHearingStatement.setStatementSender(StatementSenderMapper.toEntity(timetableHearingStatementModel.getStatementSender()));
    return timetableHearingStatement;
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

}
