package ch.sbb.line.directory.module.line.service;

import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LineVersionV3ReadService {

  private final LineVersionV3Repository lineVersionV3Repository;

  public List<LineVersionV3> findAllBySlnidOrderByValidFrom(String slnid) {
    return lineVersionV3Repository.findAllBySlnidOrderByValidFrom(slnid);
  }

  public List<LineVersionV3> findAllByReferencedLineSlnid(String slnid) {
    return lineVersionV3Repository.findAllByReferencedLineSlnid(slnid);
  }

  public List<LineVersionV3> findSwissLineNumberOverlaps(LineVersionV3 version) {
    return lineVersionV3Repository.findSwissLineNumberOverlaps(version);
  }

}
