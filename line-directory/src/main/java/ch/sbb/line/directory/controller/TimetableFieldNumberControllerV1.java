package ch.sbb.line.directory.controller;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiV1;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.exception.TtfnidNotFoundException;
import ch.sbb.line.directory.service.TimetableFieldNumberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TimetableFieldNumberControllerV1 implements TimetableFieldNumberApiV1 {

  private final TimetableFieldNumberService timetableFieldNumberService;

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
  public List<TimetableFieldNumberVersionModel> getAllVersionsVersioned(String ttfnId) {
    List<TimetableFieldNumberVersionModel> timetableFieldNumberVersionModels =
        timetableFieldNumberService.getAllVersionsVersioned(ttfnId)
            .stream()
            .map(TimetableFieldNumberControllerV1::toModel)
            .toList();
    if (timetableFieldNumberVersionModels.isEmpty()) {
      throw new TtfnidNotFoundException(ttfnId);
    }
    return timetableFieldNumberVersionModels;
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
