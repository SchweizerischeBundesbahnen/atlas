package ch.sbb.line.directory.service;

import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.controller.NotFoundExcpetion;
import ch.sbb.line.directory.entity.Line;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.repository.LineRepository;
import ch.sbb.line.directory.repository.LineVersionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LineService {

  private final LineVersionRepository lineVersionRepository;
  private final LineRepository lineRepository;
  private final VersionableService versionableService;

  public Page<Line> findAll(Pageable pageable) {
    return lineRepository.findAll(pageable);
  }

  public List<LineVersion> findLine(String slnid) {
    return lineVersionRepository.findAllBySlnid(slnid);
  }

  public Optional<LineVersion> findById(Long id) {
    return lineVersionRepository.findById(id);
  }

  public LineVersion save(LineVersion lineVersion) {
    lineVersion.setStatus(Status.ACTIVE);
    if (!lineVersionRepository.hasUniqueSwissLineNumber(lineVersion)) {
      throw new ConflictExcpetion();
    }
    return lineVersionRepository.save(lineVersion);
  }

  public void deleteById(Long id) {
    if (!lineVersionRepository.existsById(id)) {
      throw NotFoundExcpetion.getInstance().get();
    }
    lineVersionRepository.deleteById(id);
  }

  public void updateVersion(LineVersion currentVersion,
      LineVersion editedVersion) {
    List<LineVersion> currentVersions = lineVersionRepository.findAllBySlnid(
        currentVersion.getSlnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);

    versionableService.applyVersioning(LineVersion.class, versionedObjects, this::save,
        this::deleteById);
  }
}
