package ch.sbb.atlas.service;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Pageable;

@UtilityClass
public class OverviewService {

  public static <T> Container<T> toPagedContainer(List<T> elements, Pageable pageable) {
    return Container.<T>builder()
        .objects(elements.stream()
            .skip(Math.multiplyFull(pageable.getPageNumber(), pageable.getPageSize()))
            .limit(pageable.getPageSize())
            .toList())
        .totalCount(elements.size())
        .build();
  }

  public static <T extends Versionable> List<T> mergeVersionsForDisplay(List<T> versionsToMerge,
      Function<T, String> swissIdExtractor) {
    if (versionsToMerge.isEmpty()) {
      return Collections.emptyList();
    }
    List<T> result = new ArrayList<>();

    Map<String, List<T>> groupedVersions = versionsToMerge.stream().collect(Collectors.groupingBy(swissIdExtractor));
    groupedVersions.values().forEach(versions -> result.add(getDisplayModel(versions)));

    return result;
  }

  private static <T extends Versionable> T getDisplayModel(List<T> versions) {
    List<T> sortedVersions = versions.stream().sorted(Comparator.comparing(T::getValidFrom)).toList();

    T versionToShow = getPrioritizedVersion(sortedVersions);
    versionToShow.setValidFrom(sortedVersions.getFirst().getValidFrom());
    versionToShow.setValidTo(sortedVersions.getLast().getValidTo());
    return versionToShow;
  }

  private static <T extends Versionable> T getPrioritizedVersion(List<T> versions) {
    Optional<T> validToday = versions.stream().filter(i -> DateRange.fromVersionable(i).contains(LocalDate.now())).findFirst();
    Optional<T> validInFuture = versions.stream().filter(i -> i.getValidFrom().isAfter(LocalDate.now())).findFirst();
    return validToday.orElse(validInFuture.orElse(versions.getLast()));
  }
}
