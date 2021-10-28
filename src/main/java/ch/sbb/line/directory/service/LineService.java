package ch.sbb.line.directory.service;

import ch.sbb.line.directory.controller.NotFoundExcpetion;
import ch.sbb.line.directory.entity.LineVersion;
import ch.sbb.line.directory.repository.LineVersionRepository;
import ch.sbb.line.directory.swiss.number.SwissNumberUniqueValidator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LineService {

  private final LineVersionRepository lineVersionRepository;
  private final SwissNumberUniqueValidator swissNumberUniqueValidator;

  public Page<LineVersion> findAll(Pageable pageable) {
    return lineVersionRepository.findAll(pageable);
  }

  public long totalCount() {
    return lineVersionRepository.count();
  }

  public Optional<LineVersion> findById(Long id) {
    return lineVersionRepository.findById(id);
  }

  public LineVersion save(LineVersion lineVersion) {
    if (!swissNumberUniqueValidator.hasUniqueBusinessIdOverTime(lineVersion)) {
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
}
