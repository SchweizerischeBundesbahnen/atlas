package ch.sbb.line.directory.controller;

import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiV1;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberModel;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.exception.TtfnidNotFoundException;
import ch.sbb.line.directory.model.search.TimetableFieldNumberSearchRestrictions;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import ch.sbb.line.directory.service.export.TimetableFieldNumberVersionExportService;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TimetableFieldNumberController implements TimetableFieldNumberApiV1 {

  private final TimetableFieldNumberService timetableFieldNumberService;

  private final TimetableFieldNumberVersionExportService versionExportService;

  static TimetableFieldNumberVersionModel toModel(TimetableFieldNumberVersion version) {
    return TimetableFieldNumberVersionModel.builder()
        .id(version.getId())
        .description(version.getDescription())
        .number(version.getNumber())
        .ttfnid(version.getTtfnid())
        .swissTimetableFieldNumber(version.getSwissTimetableFieldNumber())
        .status(version.getStatus())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .businessOrganisation(version.getBusinessOrganisation())
        .comment(version.getComment())
        .creator(version.getCreator())
        .creationDate(version.getCreationDate())
        .editor(version.getEditor())
        .editionDate(version.getEditionDate())
        .etagVersion(version.getVersion())
        .build();
  }

  @Override
  public Container<TimetableFieldNumberModel> getOverview(Pageable pageable,
      List<String> searchCriteria, String number, String businessOrganisation,
      LocalDate validOn, List<Status> statusChoices) {
    log.info(
        "Load TimetableFieldNumbers using pageable={}, searchCriteriaSpecification={}, validOn={} and statusChoices={}",
        pageable, searchCriteria, validOn, statusChoices);
    Page<TimetableFieldNumber> timetableFieldNumberPage = timetableFieldNumberService.getVersionsSearched(
        TimetableFieldNumberSearchRestrictions.builder()
            .pageable(pageable)
            .searchCriterias(searchCriteria)
            .number(number)
            .statusRestrictions(statusChoices)
            .validOn(Optional.ofNullable(validOn))
            .businessOrganisation(Optional.ofNullable(businessOrganisation))
            .build());
    List<TimetableFieldNumberModel> versions = timetableFieldNumberPage.stream().map(this::toModel)
        .toList();
    return Container.<TimetableFieldNumberModel>builder()
        .objects(versions)
        .totalCount(timetableFieldNumberPage.getTotalElements())
        .build();
  }

  private TimetableFieldNumberModel toModel(TimetableFieldNumber version) {
    return TimetableFieldNumberModel.builder()
        .description(version.getDescription())
        .number(version.getNumber())
        .ttfnid(version.getTtfnid())
        .swissTimetableFieldNumber(
            version.getSwissTimetableFieldNumber())
        .status(version.getStatus())
        .businessOrganisation(version.getBusinessOrganisation())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .build();
  }

  @Override
  public List<TimetableFieldNumberVersionModel> getAllVersionsVersioned(String ttfnId) {
    List<TimetableFieldNumberVersionModel> timetableFieldNumberVersionModels =
        timetableFieldNumberService.getAllVersionsVersioned(ttfnId)
            .stream()
            .map(TimetableFieldNumberController::toModel)
            .toList();
    if (timetableFieldNumberVersionModels.isEmpty()) {
      throw new TtfnidNotFoundException(ttfnId);
    }
    return timetableFieldNumberVersionModels;
  }

  @Override
  public List<TimetableFieldNumberVersionModel> revokeTimetableFieldNumber(String ttfnId) {
    List<TimetableFieldNumberVersionModel> versions = timetableFieldNumberService.revokeTimetableFieldNumber(ttfnId)
        .stream()
        .map(TimetableFieldNumberController::toModel)
        .toList();
    if (versions.isEmpty()) {
      throw new TtfnidNotFoundException(ttfnId);
    }
    return versions;
  }

  @Override
  public TimetableFieldNumberVersionModel createVersion(
      TimetableFieldNumberVersionModel newVersion) {
    newVersion.setStatus(Status.VALIDATED);
    TimetableFieldNumberVersion createdVersion = timetableFieldNumberService.create(
        toEntity(newVersion));
    return toModel(createdVersion);
  }

  @Override
  public List<TimetableFieldNumberVersionModel> updateVersionWithVersioning(Long id,
      TimetableFieldNumberVersionModel newVersion) {
    TimetableFieldNumberVersion versionToUpdate = timetableFieldNumberService.findById(id)
        .orElseThrow(() ->
            new IdNotFoundException(
                id));
    timetableFieldNumberService.update(versionToUpdate, toEntity(newVersion), timetableFieldNumberService.getAllVersionsVersioned(
        versionToUpdate.getTtfnid()));
    return getAllVersionsVersioned(versionToUpdate.getTtfnid());
  }

  @Override
  public void deleteVersions(String ttfnid) {
    List<TimetableFieldNumberVersion> allVersionsVersioned = timetableFieldNumberService.getAllVersionsVersioned(
        ttfnid);
    if (allVersionsVersioned.isEmpty()) {
      throw new TtfnidNotFoundException(ttfnid);
    }
    timetableFieldNumberService.deleteAll(allVersionsVersioned);
  }

  @Override
  public List<URL> exportFullTimetableFieldNumberVersions() {
    return versionExportService.exportFullVersions();
  }

  @Override
  public List<URL> exportActualTimetableFieldNumberVersions() {
    return versionExportService.exportActualVersions();
  }

  @Override
  public List<URL> exportTimetableYearChangeTimetableFieldNumberVersions() {
    return versionExportService.exportFutureTimetableVersions();
  }

  private TimetableFieldNumberVersion toEntity(
      TimetableFieldNumberVersionModel timetableFieldNumberVersionModel) {
    return TimetableFieldNumberVersion.builder()
        .id(timetableFieldNumberVersionModel.getId())
        .description(timetableFieldNumberVersionModel.getDescription())
        .number(timetableFieldNumberVersionModel.getNumber())
        .swissTimetableFieldNumber(timetableFieldNumberVersionModel.getSwissTimetableFieldNumber())
        .status(timetableFieldNumberVersionModel.getStatus())
        .validFrom(timetableFieldNumberVersionModel.getValidFrom())
        .validTo(timetableFieldNumberVersionModel.getValidTo())
        .businessOrganisation(timetableFieldNumberVersionModel.getBusinessOrganisation())
        .comment(timetableFieldNumberVersionModel.getComment())
        .creationDate(timetableFieldNumberVersionModel.getCreationDate())
        .creator(timetableFieldNumberVersionModel.getCreator())
        .editionDate(timetableFieldNumberVersionModel.getEditionDate())
        .editor(timetableFieldNumberVersionModel.getEditor())
        .version(timetableFieldNumberVersionModel.getEtagVersion())
        .build();
  }
}
