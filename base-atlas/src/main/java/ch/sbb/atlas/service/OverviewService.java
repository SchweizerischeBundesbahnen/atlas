package ch.sbb.atlas.service;

import ch.sbb.atlas.api.model.Container;
import ch.sbb.atlas.model.DateRange;
import ch.sbb.atlas.versioning.model.Versionable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
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
      BiPredicate<T, T> equalPredicate) {
    if (versionsToMerge.isEmpty()) {
      return Collections.emptyList();
    }

    List<T> result = new ArrayList<>();

    List<T> versions = new ArrayList<>();
    Iterator<T> iterator = versionsToMerge.iterator();
    T previous = iterator.next();
    versions.add(previous);
    T current;

    while (iterator.hasNext()) {
      current = iterator.next();
      if (equalPredicate.test(previous, current)) {
        versions.add(current);
      } else {
        result.add(getDisplayModel(versions));
        versions.clear();
        versions.add(current);
      }
      previous = current;
    }
    result.add(getDisplayModel(versions));

    return result;
  }

  private static <T extends Versionable> T getDisplayModel(List<T> versions) {
    List<T> sortedVersions = versions.stream().sorted(Comparator.comparing(T::getValidFrom)).toList();

    T versionToShow = getPrioritizedVersion(sortedVersions);
    versionToShow.setValidFrom(sortedVersions.get(0).getValidFrom());
    versionToShow.setValidTo(sortedVersions.get(sortedVersions.size() - 1).getValidTo());
    return versionToShow;
  }

  private static <T extends Versionable> T getPrioritizedVersion(List<T> versions) {
    Optional<T> validToday = versions.stream().filter(i -> DateRange.fromVersionable(i).contains(LocalDate.now())).findFirst();
    Optional<T> validInFuture = versions.stream().filter(i -> i.getValidFrom().isAfter(LocalDate.now())).findFirst();
    return validToday.orElse(validInFuture.orElse(versions.get(versions.size() - 1)));
  }
}
