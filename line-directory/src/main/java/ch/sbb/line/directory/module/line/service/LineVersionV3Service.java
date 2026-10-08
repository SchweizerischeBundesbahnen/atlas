package ch.sbb.line.directory.module.line.service;

import ch.sbb.atlas.api.lidi.LineVersionModelV3;
import ch.sbb.atlas.service.OverviewDisplayBuilder;
import ch.sbb.line.directory.module.line.entity.LineVersionV3;
import ch.sbb.line.directory.module.line.mapper.LineVersionV3Mapper;
import ch.sbb.line.directory.module.line.repository.LineVersionV3Repository;
import ch.sbb.line.directory.module.line.search.LineVersionV3SearchRestrictions;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LineVersionV3Service {

  private final LineVersionV3Repository lineVersionV3Repository;

  public Page<LineVersionModelV3> findAll(LineVersionV3SearchRestrictions searchRestrictions) {
    Page<LineVersionV3> versions = lineVersionV3Repository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
    return new PageImpl<>(toModels(versions.getContent()), searchRestrictions.getPageable(), versions.getTotalElements());
  }

  public List<LineVersionModelV3> findBySlnid(String slnid) {
    return toModels(lineVersionV3Repository.findAllBySlnidOrderByValidFrom(slnid));
  }

  private List<LineVersionModelV3> toModels(List<LineVersionV3> versions) {
    Map<String, List<LineVersionV3>> parentsBySlnid = loadParents(versions);
    return versions.stream()
        .map(version -> LineVersionV3Mapper.toModel(version, findParent(parentsBySlnid, version)))
        .toList();
  }

  private Map<String, List<LineVersionV3>> loadParents(List<LineVersionV3> versions) {
    Set<String> referencedSlnids = versions.stream()
        .map(LineVersionV3::getReferencedLineSlnid)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
    if (referencedSlnids.isEmpty()) {
      return Map.of();
    }
    return lineVersionV3Repository.findAllBySlnidIn(referencedSlnids).stream()
        .collect(Collectors.groupingBy(LineVersionV3::getSlnid));
  }

  private static LineVersionV3 findParent(Map<String, List<LineVersionV3>> parentsBySlnid, LineVersionV3 version) {
    String referencedLineSlnid = version.getReferencedLineSlnid();
    if (referencedLineSlnid == null) {
      return null;
    }
    return selectParentVersion(parentsBySlnid.get(referencedLineSlnid));
  }

  private static LineVersionV3 selectParentVersion(List<LineVersionV3> parentVersions) {
    if (parentVersions == null || parentVersions.isEmpty()) {
      return null;
    }
    return OverviewDisplayBuilder.getPrioritizedVersion(parentVersions.stream()
        .sorted(Comparator.comparing(LineVersionV3::getValidFrom))
        .toList());
  }

}
