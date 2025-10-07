package ch.sbb.line.directory.module.ttfn.controller;

import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiV1;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberVersionModel;
import ch.sbb.atlas.model.Status;
import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.exception.TtfnidNotFoundException;
import ch.sbb.line.directory.module.ttfn.entity.TimetableFieldNumberVersion;
import ch.sbb.line.directory.module.ttfn.mapper.TimetableFieldNumberMapper;
import ch.sbb.line.directory.module.ttfn.service.TimetableFieldNumberService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TimetableFieldNumberControllerV1 implements TimetableFieldNumberApiV1 {

  private final TimetableFieldNumberService timetableFieldNumberService;

  @Override
  public List<TimetableFieldNumberVersionModel> getAllVersionsVersioned(String ttfnId) {
    List<TimetableFieldNumberVersionModel> timetableFieldNumberVersionModels =
        timetableFieldNumberService.getAllVersionsVersioned(ttfnId)
            .stream()
            .map(TimetableFieldNumberMapper::toModel)
            .toList();
    if (timetableFieldNumberVersionModels.isEmpty()) {
      throw new TtfnidNotFoundException(ttfnId);
    }
    return timetableFieldNumberVersionModels;
  }

  @Override
  public TimetableFieldNumberVersionModel createVersion(TimetableFieldNumberVersionModel newVersion) {
    newVersion.setStatus(Status.VALIDATED);
    TimetableFieldNumberVersion createdVersion = timetableFieldNumberService.create(
        TimetableFieldNumberMapper.toEntity(newVersion));
    return TimetableFieldNumberMapper.toModel(createdVersion);
  }

  @Override
  public List<TimetableFieldNumberVersionModel> updateVersionWithVersioning(Long id,
      TimetableFieldNumberVersionModel newVersion) {
    TimetableFieldNumberVersion versionToUpdate = timetableFieldNumberService.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));
    timetableFieldNumberService.update(versionToUpdate, TimetableFieldNumberMapper.toEntity(newVersion),
        timetableFieldNumberService.getAllVersionsVersioned(versionToUpdate.getTtfnid()));
    return getAllVersionsVersioned(versionToUpdate.getTtfnid());
  }

}
