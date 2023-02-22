package ch.sbb.line.directory.service;

import ch.sbb.atlas.model.exception.NotFoundException.IdNotFoundException;
import ch.sbb.line.directory.entity.LineVersionSnapshot;
import ch.sbb.line.directory.model.search.LineVersionSnapshotSearchRestrictions;
import ch.sbb.line.directory.repository.LineVersionSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LineVersionSnapshotService {

  private final LineVersionSnapshotRepository lineVersionSnapshotRepository;

  public Page<LineVersionSnapshot> findAll(LineVersionSnapshotSearchRestrictions searchRestrictions) {
    return lineVersionSnapshotRepository.findAll(searchRestrictions.getSpecification(), searchRestrictions.getPageable());
  }

  public LineVersionSnapshot getLineVersionSnapshotById(Long id) {
    return lineVersionSnapshotRepository.findById(id)
        .orElseThrow(() -> new IdNotFoundException(id));
  }
}
