package ch.sbb.line.directory.controller;

import ch.sbb.line.directory.api.Container;
import ch.sbb.line.directory.api.TimetableFieldNumberApiV1;
import ch.sbb.line.directory.api.TimetableFieldNumberModel;
import ch.sbb.line.directory.api.TimetableFieldNumberVersionModel;
import ch.sbb.line.directory.entity.TimetableFieldNumber;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.exception.NotFoundException.TtfnidNotFoundException;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class TimetableFieldNumberController implements TimetableFieldNumberApiV1 {

  private final TimetableFieldNumberService timetableFieldNumberService;

  @Autowired
  public TimetableFieldNumberController(TimetableFieldNumberService timetableFieldNumberService) {
    this.timetableFieldNumberService = timetableFieldNumberService;
  }

  @Override
  public Container<TimetableFieldNumberModel> getOverview(Pageable pageable,
      List<String> searchCriteria,
      LocalDate validOn, List<Status> statusChoices) {
    log.info(
        "Load TimetableFieldNumbers using pageable={}, searchCriteria={}, validOn={} and statusChoices={}",
        pageable, searchCriteria, validOn, statusChoices);
    Page<TimetableFieldNumber> timetableFieldNumberPage = timetableFieldNumberService.getVersionsSearched(
        pageable,
        searchCriteria,
        validOn, statusChoices);
    List<TimetableFieldNumberModel> versions = timetableFieldNumberPage.stream().map(this::toModel)
                                                                       .collect(
                                                                           Collectors.toList());
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
  public TimetableFieldNumberVersionModel getVersion(Long id) {
    return timetableFieldNumberService.findById(id)
                                      .map(this::toModel)
                                      .orElseThrow(() ->
                                          new IdNotFoundException(id));
  }

  @Override
  public List<TimetableFieldNumberVersionModel> getAllVersionsVersioned(String ttfnId) {
    List<TimetableFieldNumberVersionModel> timetableFieldNumberVersionModels = timetableFieldNumberService.getAllVersionsVersioned(
                                                                                                              ttfnId)
                                                                                                          .stream()
                                                                                                          .map(
                                                                                                              this::toModel)
                                                                                                          .collect(
                                                                                                              Collectors.toList());
    if (timetableFieldNumberVersionModels.isEmpty()) {
      throw new TtfnidNotFoundException(ttfnId);
    }
    return timetableFieldNumberVersionModels;
  }

  @Override
  public TimetableFieldNumberVersionModel createVersion(
      TimetableFieldNumberVersionModel newVersion) {
    newVersion.setStatus(Status.ACTIVE);
    TimetableFieldNumberVersion createdVersion = timetableFieldNumberService.save(
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
    timetableFieldNumberService.updateVersion(versionToUpdate, toEntity(newVersion));
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

  private TimetableFieldNumberVersionModel toModel(TimetableFieldNumberVersion version) {
    return TimetableFieldNumberVersionModel.builder()
                                           .id(version.getId())
                                           .description(version.getDescription())
                                           .number(version.getNumber())
                                           .ttfnid(version.getTtfnid())
                                           .swissTimetableFieldNumber(
                                               version.getSwissTimetableFieldNumber())
                                           .status(version.getStatus())
                                           .validFrom(version.getValidFrom())
                                           .validTo(version.getValidTo())
                                           .businessOrganisation(version.getBusinessOrganisation())
                                           .comment(version.getComment())
                                           .etagVersion(version.getVersion())
                                           .build();
  }

  private TimetableFieldNumberVersion toEntity(
      TimetableFieldNumberVersionModel timetableFieldNumberVersionModel) {
    return TimetableFieldNumberVersion.builder()
                                      .id(timetableFieldNumberVersionModel.getId())
                                      .description(
                                          timetableFieldNumberVersionModel.getDescription())
                                      .number(timetableFieldNumberVersionModel.getNumber())
                                      .swissTimetableFieldNumber(
                                          timetableFieldNumberVersionModel.getSwissTimetableFieldNumber())
                                      .status(timetableFieldNumberVersionModel.getStatus())
                                      .validFrom(timetableFieldNumberVersionModel.getValidFrom())
                                      .validTo(timetableFieldNumberVersionModel.getValidTo())
                                      .businessOrganisation(
                                          timetableFieldNumberVersionModel.getBusinessOrganisation())
                                      .comment(timetableFieldNumberVersionModel.getComment())
                                      .version(timetableFieldNumberVersionModel.getEtagVersion())
                                      .build();
  }
}
