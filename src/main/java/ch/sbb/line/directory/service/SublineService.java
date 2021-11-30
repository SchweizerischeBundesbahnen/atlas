package ch.sbb.line.directory.service;

import ch.sbb.atlas.versioning.model.VersionedObject;
import ch.sbb.atlas.versioning.service.VersionableService;
import ch.sbb.line.directory.controller.NotFoundExcpetion;
import ch.sbb.line.directory.entity.Subline;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.enumaration.Status;
import ch.sbb.line.directory.repository.SublineRepository;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SublineService {

  private final SublineVersionRepository sublineVersionRepository;
  private final SublineRepository sublineRepository;
  private final VersionableService versionableService;

  public Page<Subline> findAll(Pageable pageable) {
    return sublineRepository.findAll(pageable);
  }

  public List<SublineVersion> findSubline(String slnid) {
    return sublineVersionRepository.findAllBySlnid(slnid);
  }
  public Optional<SublineVersion> findById(Long id) {
    return sublineVersionRepository.findById(id);
  }

  public SublineVersion save(SublineVersion sublineVersion) {
    sublineVersion.setStatus(Status.ACTIVE);
    if (!sublineVersionRepository.hasUniqueSwissSublineNumber(sublineVersion)) {
      throw new ConflictExcpetion();
    }
    return sublineVersionRepository.save(sublineVersion);
  }

  public void deleteById(Long id) {
    if (!sublineVersionRepository.existsById(id)) {
      throw NotFoundExcpetion.getInstance().get();
    }
    sublineVersionRepository.deleteById(id);
  }

  public void updateVersion(SublineVersion currentVersion,      SublineVersion editedVersion) {
    List<SublineVersion> currentVersions = sublineVersionRepository.findAllBySlnid(
        currentVersion.getSlnid());

    List<VersionedObject> versionedObjects = versionableService.versioningObjects(currentVersion,
        editedVersion, currentVersions);

    versionableService.applyVersioning(SublineVersion.class, versionedObjects, this::save,
        this::deleteById);
  }
}
