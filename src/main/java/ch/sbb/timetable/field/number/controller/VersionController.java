package ch.sbb.timetable.field.number.controller;

import ch.sbb.timetable.field.number.api.TimetableFieldNumberApiV1;
import ch.sbb.timetable.field.number.api.TimetableFieldNumberContainer;
import ch.sbb.timetable.field.number.api.TimetableFieldNumberModel;
import ch.sbb.timetable.field.number.api.VersionModel;
import ch.sbb.timetable.field.number.entity.TimetableFieldNumber;
import ch.sbb.timetable.field.number.entity.Version;
import ch.sbb.timetable.field.number.enumaration.Status;
import ch.sbb.timetable.field.number.exceptions.BadRequestException;
import ch.sbb.timetable.field.number.service.VersionService;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RestController
@Slf4j
public class VersionController implements TimetableFieldNumberApiV1, WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.removeConvertible(String.class, Collection.class);
  }

  private static final Supplier<ResponseStatusException> NOT_FOUND_EXCEPTION = () -> new ResponseStatusException(
      HttpStatus.NOT_FOUND);

  private final VersionService versionService;

  @Autowired
  public VersionController(VersionService versionService) {
    this.versionService = versionService;
  }

  @Override
  public TimetableFieldNumberContainer getOverview(Pageable pageable, List<String> searchCriteria,
      LocalDate validOn, List<Status> statusChoices) {
    log.info("Load TimetableFieldNumbers using pageable={}, searchCriteria={}, validOn={} and statusChoices={}",
        pageable, searchCriteria, validOn, statusChoices);
    Page<TimetableFieldNumber> timetableFieldNumberPage = versionService.getVersionsSearched(pageable,
        searchCriteria,
        validOn, statusChoices);
    List<TimetableFieldNumberModel> versions = timetableFieldNumberPage.stream().map(this::toModel)
        .collect(Collectors.toList());
    return TimetableFieldNumberContainer.builder()
        .fieldNumbers(versions)
        .totalCount(timetableFieldNumberPage.getTotalElements())
        .build();
  }

  @ExceptionHandler(PropertyReferenceException.class)
  public ResponseEntity<BadRequestException> handleInvalidSort(PropertyReferenceException exception) {
    log.warn("Pageable sort parameter is not valid.", exception);
    return ResponseEntity.badRequest().body(new BadRequestException("Pageable sort parameter is not valid."));
  }

  private TimetableFieldNumberModel toModel(TimetableFieldNumber version) {
    return TimetableFieldNumberModel.builder()
        .name(version.getName())
        .ttfnid(version.getTtfnid())
        .swissTimetableFieldNumber(version.getSwissTimetableFieldNumber())
        .status(version.getStatus())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .build();
  }

  @Override
  public VersionModel getVersion(Long id) {
    return versionService.findById(id).map(this::toModel).orElseThrow(NOT_FOUND_EXCEPTION);
  }

  @Override
  public List<VersionModel> getAllVersionsVersioned(String ttfnId) {
    return versionService.getAllVersionsVersioned(ttfnId).stream().map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public VersionModel createVersion(VersionModel newVersion) {
    newVersion.setStatus(Status.ACTIVE);
    Version version = toEntity(newVersion);
    Version createdVersion = versionService.save(version);
    return toModel(createdVersion);
  }

  @Override
  public VersionModel updateVersion(Long id, VersionModel newVersion) {
    Version versionToUpdate = versionService.findById(id).orElseThrow(NOT_FOUND_EXCEPTION);

    versionToUpdate.setName(newVersion.getName());
    versionToUpdate.setNumber(newVersion.getNumber());
    versionToUpdate.setSwissTimetableFieldNumber(newVersion.getSwissTimetableFieldNumber());
    versionToUpdate.setValidFrom(newVersion.getValidFrom());
    versionToUpdate.setValidTo(newVersion.getValidTo());
    versionToUpdate.setComment(newVersion.getComment());
    versionToUpdate.setBusinessOrganisation(newVersion.getBusinessOrganisation());
    versionService.save(versionToUpdate);

    return toModel(versionToUpdate);
  }

  @Override
  public List<VersionModel> updateVersionWithVersioning(Long id, VersionModel newVersion) {
    Version versionToUpdate = versionService.findById(id).orElseThrow(NOT_FOUND_EXCEPTION);
    versionService.updateVersion(versionToUpdate, toEntity(newVersion));
    return getAllVersionsVersioned(versionToUpdate.getTtfnid());
  }

  @Override
  public void deleteVersions(String ttfnid) {
    List<Version> allVersionsVersioned = versionService.getAllVersionsVersioned(ttfnid);
    if (allVersionsVersioned.isEmpty()) {
      throw NOT_FOUND_EXCEPTION.get();
    }
    versionService.deleteAll(allVersionsVersioned);
  }

  private VersionModel toModel(Version version) {
    return VersionModel.builder()
        .id(version.getId())
        .name(version.getName())
        .number(version.getNumber())
        .ttfnid(version.getTtfnid())
        .swissTimetableFieldNumber(version.getSwissTimetableFieldNumber())
        .status(version.getStatus())
        .validFrom(version.getValidFrom())
        .validTo(version.getValidTo())
        .businessOrganisation(version.getBusinessOrganisation())
        .comment(version.getComment())
        .build();
  }

  private Version toEntity(VersionModel versionModel) {
    return Version.builder()
        .id(versionModel.getId())
        .name(versionModel.getName())
        .number(versionModel.getNumber())
        .swissTimetableFieldNumber(versionModel.getSwissTimetableFieldNumber())
        .status(versionModel.getStatus())
        .validFrom(versionModel.getValidFrom())
        .validTo(versionModel.getValidTo())
        .businessOrganisation(versionModel.getBusinessOrganisation())
        .comment(versionModel.getComment())
        .build();
  }
}
