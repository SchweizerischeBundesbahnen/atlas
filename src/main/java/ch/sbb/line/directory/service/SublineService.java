package ch.sbb.line.directory.service;

import ch.sbb.line.directory.controller.NotFoundExcpetion;
import ch.sbb.line.directory.entity.SublineVersion;
import ch.sbb.line.directory.repository.SublineVersionRepository;
import ch.sbb.line.directory.swiss.number.SwissNumberUniqueValidator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SublineService {

  private final SublineVersionRepository sublineVersionRepository;
  private final SwissNumberUniqueValidator swissNumberUniqueValidator;

  public Page<SublineVersion> findAll(Pageable pageable) {
    return sublineVersionRepository.findAll(pageable);
  }

  public long totalCount() {
    return sublineVersionRepository.count();
  }

  public Optional<SublineVersion> findById(Long id) {
    return sublineVersionRepository.findById(id);
  }

  public SublineVersion save(SublineVersion sublineVersion) {
    if (!swissNumberUniqueValidator.hasUniqueBusinessIdOverTime(sublineVersion)) {
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
}
